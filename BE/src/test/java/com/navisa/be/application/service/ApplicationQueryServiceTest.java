package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.response.RecentVisaFormsResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.application.service.ApplicationQueryService;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.foreigner.repository.ForeignerProfileRepository;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.LoginType;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ApplicationQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationQueryService visaApplicationQueryService;

    @Autowired
    private ApplicationFormRepository visaApplicationFormRepository;

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

    @DisplayName("승인된 행정사는 자신이 최근에 수정한 비자 신청서 목록을 최대 6개까지 조회할 수 있다.")
    @Test
    void getRecentVisaForms_Success() {
        // given
        String email = "agent@test.com";
        User user = saveUser(email, UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(user.getId());
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "V01", "관광비자", null, null));

        for (int i = 1; i <= 7; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(UUID.randomUUID(), null));
            VisaApplicationForm form = new VisaApplicationForm(agent, foreigner, jobCode, false, 10 + i);
            visaApplicationFormRepository.save(form);

            ReflectionTestUtils.setField(form, "updatedAt", LocalDateTime.now().minusDays(10 - i));

            em.flush();
        }
        em.clear();

        // when
        List<RecentVisaFormsResponse> result = visaApplicationQueryService.getRecentVisaForms(email);

        // then
        assertThat(result).hasSize(6);
        assertThat(result.get(0).currentStep()).isEqualTo(17); // i=7 (10+7)
        assertThat(result.get(1).currentStep()).isEqualTo(16); // i=6 (10+6)
        assertThat(result.get(0).lastModifiedAt()).isNotNull();
    }

    @DisplayName("등록된 서류가 6개 미만(예: 2개)일 경우, 존재하는 2개만 반환한다.")
    @Test
    void getRecentVisaForms_LessThanSix() {
        // given
        String email = "agent@test.com";
        User user = saveUser(email, UserType.VALID_AGENT);
        AgentProfile agent = saveAgentProfile(user.getId());
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "V01", "관광비자", null, null));

        for (int i = 1; i <= 2; i++) {
            ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(UUID.randomUUID(), null));
            visaApplicationFormRepository.save(new VisaApplicationForm(agent, foreigner, jobCode, false, 50 + i));
        }

        // when
        List<RecentVisaFormsResponse> result = visaApplicationQueryService.getRecentVisaForms(email);

        // then
        assertThat(result).hasSize(2);
    }

    @DisplayName("UserType이 VALID_AGENT가 아닌 경우 AGENT_NOT_APPROVED 예외가 발생한다.")
    @Test
    void getRecentVisaForms_NotApproved_Fail() {
        // given
        String email = "pending_agent@test.com";
        saveUser(email, UserType.INVALID_AGENT);

        // when & then
        assertThatThrownBy(() -> visaApplicationQueryService.getRecentVisaForms(email))
                .isInstanceOf(ApplicationException.class);
    }

    private User saveUser(String email, UserType type) {
        return userRepository.save(new User(
                email,
                "password123",
                type,
                LoginType.EMAIL,
                true));
    }

    private AgentProfile saveAgentProfile(UUID userId) {
        return agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                userId, "LIC123", LocalDate.now(), "P123", "M123", "코멘트"));
    }
}