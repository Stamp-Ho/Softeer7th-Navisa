package com.navisa.be.application.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.repository.JobCodeRepository;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ApplicationFormRepositoryTest extends IntegrationTestSupport {

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

    @Test
    @DisplayName("exportedAt의 날짜가 타겟 날짜와 일치하는 완료된 서류만 조회한다.")
    void findAllByExportedDate_Success() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.minusDays(14);

        // 1. 조회 대상: 14일 전 데이터 (오전 10시)
        createFormWithExportedAt("target@test.com", targetDate.atTime(10, 0), true);

        // 2. 조회 대상: 14일 전 데이터 (오후 11시 59분) - 경계값
        createFormWithExportedAt("target2@test.com", targetDate.atTime(23, 59), true);

        // 3. 제외 대상: 13일 전 데이터
        createFormWithExportedAt("minus13@test.com", today.minusDays(13).atStartOfDay(), true);

        // 4. 제외 대상: 15일 전 데이터
        createFormWithExportedAt("minus15@test.com", today.minusDays(15).atStartOfDay(), true);

        // 5. 제외 대상: 날짜는 맞지만 완료되지 않은(isDone=false) 데이터
        createFormWithExportedAt("notdone@test.com", targetDate.atTime(12, 0), false);

        // when
        List<VisaApplicationForm> result = applicationFormRepository.findAllByExportedDate(targetDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(f -> f.getExportedAt().toLocalDate().equals(targetDate));
        assertThat(result).allMatch(VisaApplicationForm::isDone);
    }

    private void createFormWithExportedAt(String email, LocalDateTime exportedAt, boolean isDone) {
        User user = userRepository.save(new User(email, "pw", UserType.VALID_AGENT, LoginType.EMAIL, true));
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                "행정사", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", user.getId(), "LIC", LocalDate.now(), "P", "M", "C"));

        User fUser = userRepository.save(new User("f_"+email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(fUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7", "특수", null, null));

        VisaApplicationForm form = new VisaApplicationForm(agent, foreigner, jobCode, isDone, 100, 0);
        ReflectionTestUtils.setField(form, "exportedAt", exportedAt);
        applicationFormRepository.save(form);
    }
}
