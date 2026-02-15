package com.navisa.be.application.scheduler;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.application.service.ApplicationEmailService;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.model.enums.ForeignerSearchStatus;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Transactional
class VisaEmailSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private VisaEmailScheduler visaEmailScheduler;

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

    @MockitoSpyBean
    private ApplicationEmailService applicationEmailService;

    @Test
    @DisplayName("스케줄러 실행 시 14일 전 내보내기 된 서류 담당자에게 메일을 발송한다.")
    void sendFollowUpEmails_Success() {
        // given
        LocalDate today = LocalDate.now();
        LocalDateTime targetDateTime = today.minusDays(14).atTime(10, 0);
        String agentEmail = "target-agent@navisa.site";
        String agentName = "김행정";

        // 1. 발송 대상 (T-14)
        createFormWithExportedAt(agentEmail, agentName, targetDateTime, true);

        // 2. 제외 대상 (T-13)
        createFormWithExportedAt("other@navisa.site", "이행정", today.minusDays(13).atStartOfDay(), true);

        // when
        visaEmailScheduler.sendFollowUpEmails();

        // then
        verify(applicationEmailService, times(1))
                .sendCareEmail(eq(agentEmail), eq(agentName));

        verify(applicationEmailService, never())
                .sendCareEmail(eq("other@navisa.site"), anyString());
    }

    private void createFormWithExportedAt(String email, String name, LocalDateTime exportedAt, boolean isDone) {
        User user = userRepository.save(new User(email, "pw", UserType.VALID_AGENT, LoginType.EMAIL, true));
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                name, LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", user.getId(), "LIC", LocalDate.now(), "P", "M", "C"));

        User fUser = userRepository.save(new User("f_" + UUID.randomUUID(), "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(fUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수", null, null));

        VisaApplicationForm form = new VisaApplicationForm(agent, foreigner, jobCode, isDone, 100, 0);
        ReflectionTestUtils.setField(form, "exportedAt", exportedAt);
        applicationFormRepository.save(form);
    }
}
