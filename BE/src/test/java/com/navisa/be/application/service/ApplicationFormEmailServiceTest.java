package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private AgentProfileRepository agentProfileRepository;

    @Autowired
    private ForeignerProfileRepository foreignerProfileRepository;

    @Autowired
    private JobCodeRepository jobCodeRepository;

    @Autowired
    private EntityManager em;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    @DisplayName("비자 신청서 상태를 완료(isDone=true)로 변경하면 exportedAt 시각이 기록된다.")
    void updateApplicationStatus_RecordsExportedAt() {
        // given
        String email = "agent@navisa.com";
        User loginUser = saveUser(email, UserType.VALID_AGENT);
        ApplicationForm form = setupInitialForm(loginUser);
        UUID visaFormId = form.getId();

        // when
        applicationFormForAgentService.updateApplicationStatus(email, visaFormId, true);
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
        String email = "agent@navisa.com";
        User loginUser = saveUser(email, UserType.VALID_AGENT);
        ApplicationForm form = setupInitialForm(loginUser);

        applicationFormForAgentService.updateApplicationStatus(email, form.getId(), true);
        em.flush();
        em.clear();

        java.time.LocalDateTime firstExportedAt = applicationFormRepository.findById(form.getId()).get().getExportedAt();

        // when
        applicationFormForAgentService.updateApplicationStatus(email, form.getId(), true);
        em.flush();
        em.clear();

        // then
        java.time.LocalDateTime secondExportedAt = applicationFormRepository.findById(form.getId()).get().getExportedAt();
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

    private ApplicationForm setupInitialForm(User owner) {
        AgentProfile agent = saveAgentProfile(owner.getId());
        User foreignerUser = saveUser("foreigner_owner@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(foreignerUser.getId(), null));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수활동", null, null));
        return applicationFormRepository.save(new ApplicationForm(agent, foreigner, jobCode, false, 100, 0));
    }

    private User saveUser(String email, UserType type) {
        return userRepository.save(new User(email, "pw", type, LoginType.EMAIL, true));
    }

    private AgentProfile saveAgentProfile(UUID userId) {
        return agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", userId, "LIC123", LocalDate.now(), "P123", "M123", "코멘트"));
    }
}
