package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.support.*;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@Transactional
public class ApplicationFormEmailServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationFormForAgentService applicationFormForAgentService;

    @Autowired
    private ApplicationFormEmailService applicationFormEmailService;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private UserTestFixture userTestFixture;

    @Autowired
    private AgentProfileTestFixture agentProfileTestFixture;

    @Autowired
    private ForeignerProfileTestFixture foreignerProfileTestFixture;

    @Autowired
    private ChatRoomTestFixture chatRoomTestFixture;

    @Autowired
    private VisaApplicationFormTestFixture visaApplicationFormTestFixture;

    @Autowired
    private EntityManager em;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    @DisplayName("비자 신청서 상태를 완료(isDone=true)로 변경하면 exportedAt 시각이 기록된다.")
    void updateApplicationStatus_RecordsExportedAt() {
        // given
        User agentUser = userTestFixture.createUser("agent@navisa.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("김행정", "서울", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@navisa.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                LocalDate.now().atStartOfDay().plusHours(9).atZone(ZoneId.systemDefault()));

        JobCode jobCode = agentProfileTestFixture.createJobCode("E7", "특수활동");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, false);

        UUID visaFormId = form.getId();

        // when
        applicationFormForAgentService.updateApplicationStatus(agentUser.getEmail(), visaFormId, true);
        em.flush();
        em.clear();

        // then
        ApplicationForm updatedForm = applicationFormRepository.findById(visaFormId).orElseThrow();
        assertThat(updatedForm.isDone()).isTrue();
        assertThat(updatedForm.getExportedAt()).isNotNull();
    }

    @Test
    @DisplayName("이미 완료된 서류의 상태를 다시 true로 변경해도 exportedAt 시각은 변하지 않는다.")
    void updateApplicationStatus_DoesNotOverrideExportedAt() {
        // given
        User agentUser = userTestFixture.createUser("agent@navisa.com", UserType.VALID_AGENT);
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("김행정", "서울", agentUser.getId());

        User foreignerUser = userTestFixture.createUser("foreigner@navisa.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT,
                LocalDate.now().atStartOfDay().plusHours(9).atZone(ZoneId.systemDefault()));

        JobCode jobCode = agentProfileTestFixture.createJobCode("E7", "특수활동");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, false);

        applicationFormForAgentService.updateApplicationStatus(agentUser.getEmail(), form.getId(), true);
        em.flush();
        em.clear();

        LocalDateTime firstExportedAt = applicationFormRepository.findById(form.getId())
                .orElseThrow()
                .getExportedAt();


        // when
        applicationFormForAgentService.updateApplicationStatus(agentUser.getEmail(), form.getId(), true);
        em.flush();
        em.clear();

        // then
        LocalDateTime secondExportedAt = applicationFormRepository.findById(form.getId())
                .orElseThrow()
                .getExportedAt();

        assertThat(firstExportedAt).isNotNull();
        assertThat(secondExportedAt).isEqualTo(firstExportedAt);
    }

    @Test
    @DisplayName("메일 발송 메서드가 JavaMailSender를 정상적으로 호출한다")
    void sendEmail_CallCheck() {
        // given
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        given(mailSender.createMimeMessage()).willReturn(mimeMessage);

        // when
        CompletableFuture<Boolean> result = applicationFormEmailService.sendCareEmail("agent@test.com", "김행정");
        result.join();

        // then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
        verifyNoMoreInteractions(mailSender);
    }
}
