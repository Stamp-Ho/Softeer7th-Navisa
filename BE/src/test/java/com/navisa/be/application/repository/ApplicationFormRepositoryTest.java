package com.navisa.be.application.repository;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.model.entity.ApplicationForm;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        List<ApplicationForm> result = applicationFormRepository.findAllByExportedDate(targetDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(f -> f.getExportedAt().toLocalDate().equals(targetDate));
        assertThat(result).allMatch(ApplicationForm::isDone);
    }

    @Test
    @DisplayName("findWithAgentProfileAndForeignerProfileById는 agentProfile과 foreignerProfile을 함께 로드한다.")
    void findWithAgentProfileAndForeignerProfileById_Success() {
        // given
        User agentUser = userRepository
                .save(new User("agent-joined@test.com", "pw", UserType.VALID_AGENT, LoginType.EMAIL, true));
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                "행정사", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", agentUser.getId(), "LIC-JOINED", LocalDate.now(), "P", "M", "C"));

        User fUser = userRepository
                .save(new User("foreigner-joined@test.com", "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));
        ForeignerProfile foreigner = foreignerProfileRepository
                .save(new ForeignerProfile(fUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7-J", "특수", null, null));

        ApplicationForm form = applicationFormRepository
                .save(new ApplicationForm(agent, foreigner, jobCode, false, 100, 0));

        // when
        Optional<ApplicationForm> result = applicationFormRepository
                .findWithAgentProfileAndForeignerProfileById(form.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getAgentProfile()).isNotNull();
        assertThat(result.get().getAgentProfile().getId()).isEqualTo(agent.getId());
        assertThat(result.get().getForeignerProfile()).isNotNull();
        assertThat(result.get().getForeignerProfile().getId()).isEqualTo(foreigner.getId());
    }

    @Test
    @DisplayName("findFirstWithAgentProfileAndForeignerProfileByForeignerProfile_UserIdOrderByCreatedAtDesc는 외국인의 최신 신청서를 agentProfile, foreignerProfile과 함께 반환한다.")
    void findFirstWithAgentProfileAndForeignerProfileByForeignerProfile_UserId_Success() {
        // given
        User agentUser = userRepository
                .save(new User("agent-fst@test.com", "pw", UserType.VALID_AGENT, LoginType.EMAIL, true));
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                "행정사", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", agentUser.getId(), "LIC-FST", LocalDate.now(), "P", "M", "C"));

        User fUser = userRepository
                .save(new User("foreigner-fst@test.com", "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));
        ForeignerProfile foreigner = foreignerProfileRepository
                .save(new ForeignerProfile(fUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7-FST", "특수", null, null));

        applicationFormRepository.save(new ApplicationForm(agent, foreigner, jobCode, false, 100, 0));
        ApplicationForm latestForm = applicationFormRepository
                .save(new ApplicationForm(agent, foreigner, jobCode, false, 100, 0));

        // when
        Optional<ApplicationForm> result = applicationFormRepository
                .findFirstWithAgentProfileAndForeignerProfileByForeignerProfile_UserIdOrderByCreatedAtDesc(
                        fUser.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(latestForm.getId());
        assertThat(result.get().getAgentProfile()).isNotNull();
        assertThat(result.get().getAgentProfile().getId()).isEqualTo(agent.getId());
        assertThat(result.get().getForeignerProfile()).isNotNull();
        assertThat(result.get().getForeignerProfile().getId()).isEqualTo(foreigner.getId());
    }

    @Test
    @DisplayName("findAllRequiringEnd는 isFinished가 false이고 exportedAt의 날짜가 targetDate와 일치하는 서류를 조회한다.")
    void findAllRequiringEnd_Success() {
        // given
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.minusDays(17); // 17일 전

        // 조회 대상
        // 1차 내보내기 완료 후 정해진 기간이 흐름
        createFormWithExportedAtAndFinished("target_req@test.com", targetDate.atTime(10, 0), true, false);
        createFormWithExportedAtAndFinished("target_req2@test.com", targetDate.atTime(23, 59), true, false);

        // 제외 대상
        createFormWithExportedAtAndFinished("minus6@test.com", today.minusDays(6).atStartOfDay(), true, false);
        createFormWithExportedAtAndFinished("minus8@test.com", today.minusDays(8).atStartOfDay(), false, false);
        createFormWithExportedAtAndFinished("finished@test.com", targetDate.atTime(12, 0), true, true);

        // when
        List<ApplicationForm> result = applicationFormRepository.findAllRequiringEnd(targetDate);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(f -> f.getExportedAt().toLocalDate().equals(targetDate));
        assertThat(result).allMatch(f -> !f.isFinished());
    }

    private void createFormWithExportedAt(String email, LocalDateTime exportedAt, boolean isDone) {
        createFormWithExportedAtAndFinished(email, exportedAt, isDone, false);
    }

    private void createFormWithExportedAtAndFinished(String email, LocalDateTime exportedAt, boolean isDone, boolean isFinished) {
        User user = userRepository.save(new User(email, "pw", UserType.VALID_AGENT, LoginType.EMAIL, true));
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                "행정사", LocalDate.now(), "key", "09:00", "사무소", "주소", "상세", "이력",
                "010-1234-1234", user.getId(), "LIC", LocalDate.now(), "P", "M", "C"));

        User fUser = userRepository
                .save(new User("f_" + email, "pw", UserType.FILLED_FOREIGNER, LoginType.EMAIL, true));
        ForeignerProfile foreigner = foreignerProfileRepository
                .save(new ForeignerProfile(fUser.getId(), ForeignerSearchStatus.REQUESTING));
        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E7" + email, "특수", null, null));

        ApplicationForm form = new ApplicationForm(agent, foreigner, jobCode, isDone, 100, 0);
        ReflectionTestUtils.setField(form, "exportedAt", exportedAt);
        if (isFinished) {
            form.updateFinish(true);
        }
        applicationFormRepository.save(form);
    }
}
