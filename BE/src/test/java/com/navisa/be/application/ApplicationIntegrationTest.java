package com.navisa.be.application;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.application.service.ApplicationFormForAgentService;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ApplicationIntegrationTest extends IntegrationTestSupport {

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
    private ApplicationFormForAgentService applicationFormForAgentService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("행정사가 수임 종료를 요청하면 전체 비자 갱신 프로세스가 완료된다.")
    void visaProcess_FullCycle_Integration() {
        User agentUser = saveUser("agent@navisa.com", UserType.VALID_AGENT);
        ApplicationForm form = setupInitialForm(agentUser);

        // 수임 종료 API 호출
        ApplicationFormFinishedStatusResponse response = applicationFormForAgentService
                .finishApplication(agentUser.getEmail(), form.getId(), true);

        // 데이터베이스 최종 상태 검증
        List<ApplicationForm> allForms = applicationFormRepository.findAll();

        // 기존 폼 1개 + 신규 폼 1개 = 총 2개
        assertThat(allForms).hasSize(2);

        // 기존 폼 상태 확인: isFinished = true
        ApplicationForm updatedOldForm = applicationFormRepository.findById(form.getId()).get();
        assertThat(updatedOldForm.isFinished()).isTrue();

        // 신규 폼 상태 확인: 행정사 정보가 없고 새롭게 시작 가능한 상태
        ApplicationForm nextCycle = applicationFormRepository.findById(response.newVisaFormId()).get();
        assertThat(nextCycle.getAgentProfile()).isNull(); // 행정사 null
        assertThat(nextCycle.isDone()).isFalse();
        assertThat(nextCycle.isFinished()).isFalse();

        // 기존 외국인 정보 및 섹션 데이터 유지 확인
        assertThat(nextCycle.getForeignerProfile().getId()).isEqualTo(form.getForeignerProfile().getId());
        assertThat(nextCycle.getPersonalDetail()).isEqualTo(form.getPersonalDetail());
    }

    private User saveUser(String email, UserType userType) {
        // 빌더 대신 일반 생성자 사용
        return userRepository.save(new User(
                email,
                "password123!",
                userType,
                LoginType.EMAIL,
                true));
    }

    private ApplicationForm setupInitialForm(User agentUser) {
        AgentProfile agent = agentProfileRepository.save(new AgentProfile(
                "김행정", LocalDate.of(1980, 1, 1), "profile_key", "09:00-18:00",
                "나비사 행정사무소", "서울시 강남구", "테헤란로 123", "자기소개",
                "010-1234-5678", agentUser.getId(), "LIC-123", LocalDate.now(),
                "seal_key", "cert_key", "매니저 코멘트"));

        User foreignerUser = saveUser("foreigner@navisa.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile foreigner = foreignerProfileRepository.save(new ForeignerProfile(
                foreignerUser.getId(),
                ForeignerSearchStatus.REQUESTING));

        JobCode jobCode = jobCodeRepository.save(new JobCode(null, "E-7", "특수활동", null, null));

        ApplicationForm form = new ApplicationForm(agent, foreigner, jobCode, false, 150, 1);

        Map<String, Object> sampleSection = Map.of("sectionId", 1, "fields",
                List.of(Map.of("fieldId", 101, "value", "Hong")));
        form.updateSections(List.of(sampleSection), 150, 10);

        applicationFormRepository.save(form);

        // 수임 종료 후 피드백 메세지 전송을 위해 ChatRoom 필요
        chatRoomRepository.save(new ChatRoom(foreigner, agent, ChatRoomStatus.DEFAULT));

        return form;
    }
}
