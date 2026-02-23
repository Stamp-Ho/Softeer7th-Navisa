package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.dto.response.ApplicationFormDetailResponse;
import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.support.AgentProfileTestFixture;
import com.navisa.be.support.ChatRoomTestFixture;
import com.navisa.be.support.ForeignerProfileTestFixture;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.support.UserTestFixture;
import com.navisa.be.support.VisaApplicationFormTestFixture;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
class ApplicationFormForForeignerServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationFormForForeignerService applicationFormForForeignerService;

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

    @MockitoSpyBean
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ApplicationFormSearchService applicationFormSearchService;

    @Test
    @DisplayName("외국인이 수임 종료 시 신청서 상태가 변경되고, agent의 userId로 FEEDBACK_REQUIRED 메세지가 저장된다.")
    void finishByForeigner_SavesFeedbackMessage() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "서울", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        JobCode jobCode = agentProfileTestFixture.createJobCode("E7", "특수직");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, true);

        // 메일 발송 후 3일 경과 조건 충족
        ReflectionTestUtils.setField(form, "mailSentAt", LocalDateTime.now().minusDays(4));
        applicationFormRepository.saveAndFlush(form);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // when
        ApplicationFormFinishedStatusResponse response = applicationFormForForeignerService
                .finishByForeigner(foreignerUser.getEmail());

        // then - 응답 검증
        assertThat(response).isNotNull();
        assertThat(response.closedVisaFormId()).isEqualTo(form.getId());
        assertThat(response.newVisaFormId()).isNotNull();
    }

    @Test
    @DisplayName("이미 종료된 신청서에 finishByForeigner 호출 시 ALREADY_FINISHED 예외가 발생한다.")
    void finishByForeigner_ThrowsWhenAlreadyFinished() {
        // given
        User agentUser = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner2@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "부산", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        JobCode jobCode = agentProfileTestFixture.createJobCode("E8", "기타직");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, true);
        ReflectionTestUtils.setField(form, "isFinished", true);
        applicationFormRepository.saveAndFlush(form);

        // when & then
        assertThatThrownBy(() -> applicationFormForForeignerService.finishByForeigner(foreignerUser.getEmail()))
                .isInstanceOf(ApplicationFormException.class)
                .hasMessageContaining(ResponseStatus.ALREADY_FINISHED.getMessage());
    }

    @Test
    @DisplayName("메일 발송 후 3일 미경과 시 finishByForeigner 호출 시 FINISH_CONDITION_NOT_MET 예외가 발생한다.")
    void finishByForeigner_ThrowsWhenFinishConditionNotMet() {
        // given
        User agentUser = userTestFixture.createUser("agent3@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner3@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "대구", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        JobCode jobCode = agentProfileTestFixture.createJobCode("E9", "비숙련직");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, true);
        // 1일 전 메일 발송 - 3일 미경과
        ReflectionTestUtils.setField(form, "mailSentAt", LocalDateTime.now().minusDays(1));
        applicationFormRepository.saveAndFlush(form);

        // when & then
        assertThatThrownBy(() -> applicationFormForForeignerService.finishByForeigner(foreignerUser.getEmail()))
                .isInstanceOf(ApplicationFormException.class)
                .hasMessageContaining(ResponseStatus.FINISH_CONDITION_NOT_MET.getMessage());
    }

    @Test
    @DisplayName("최신 신청서가 존재하지 않을 때 finishByForeigner 호출 시 VISA_APP_FORM_NOT_FOUND 예외가 발생한다.")
    void finishByForeigner_ThrowsWhenFormNotFound() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner4@test.com", UserType.FILLED_FOREIGNER);
        foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        // 신청서 없이 외국인 프로파일만 생성

        // when & then
        assertThatThrownBy(() -> applicationFormForForeignerService.finishByForeigner(foreignerUser.getEmail()))
                .isInstanceOf(ApplicationFormException.class)
                .hasMessageContaining(ResponseStatus.VISA_APP_FORM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("이미지 키가 공백(Blank)인 경우에도 복제 및 조회가 정상적으로 수행된다.")
    void finishByForeigner_WithBlankImageKey_Success() {
        // given
        User foreignerUser = userTestFixture.createUser("foreigner_blank@test.com", UserType.FILLED_FOREIGNER);
        User agentUser = userTestFixture.createUser("agent_blank@test.com", UserType.VALID_AGENT);

        AgentProfile agent = agentProfileTestFixture.createAgentProfile("행정사", "인천", agentUser.getId());
        ForeignerProfile foreigner = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        // 이미지 키를 빈 문자열("")로 설정
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(
                agent, foreigner, agentProfileTestFixture.createJobCode("E7", "특수"), true);
        ReflectionTestUtils.setField(form, "profileObjectKey", "");
        ReflectionTestUtils.setField(form, "mailSentAt", LocalDateTime.now().minusDays(4));
        applicationFormRepository.saveAndFlush(form);

        chatRoomTestFixture.createChatRoom(foreigner, agent, ChatRoomStatus.DEFAULT);

        // when
        ApplicationFormFinishedStatusResponse response = applicationFormForForeignerService.finishByForeigner(foreignerUser.getEmail());

        // then
        ApplicationForm newForm = applicationFormRepository.findById(response.newVisaFormId()).get();
        assertThat(newForm.getProfileObjectKey()).isEqualTo("");
    }

    @Test
    @DisplayName("이미지(profileObjectKey)가 없는 신청서를 종료하고 복제할 때, 새로운 신청서의 이미지 키도 null로 유지된다.")
    void finishByForeigner_WithNoImage_Success() {
        // given
        User agentUser = userTestFixture.createUser("agent_noimg@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner_noimg@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "제주", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);
        JobCode jobCode = agentProfileTestFixture.createJobCode("E7", "특수직");

        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(
                agentProfile, foreignerProfile, jobCode, true);

        ReflectionTestUtils.setField(form, "profileObjectKey", null);
        ReflectionTestUtils.setField(form, "mailSentAt", LocalDateTime.now().minusDays(5));
        applicationFormRepository.saveAndFlush(form);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // when
        ApplicationFormFinishedStatusResponse response = applicationFormForForeignerService
                .finishByForeigner(foreignerUser.getEmail());

        // then
        ApplicationForm oldForm = applicationFormRepository.findById(response.closedVisaFormId()).get();
        ApplicationForm newForm = applicationFormRepository.findById(response.newVisaFormId()).get();

        assertThat(oldForm.getProfileObjectKey()).isNull();
        assertThat(newForm.getProfileObjectKey()).isNull();
        assertThat(newForm.isFinished()).isFalse();
    }


    @Test
    @DisplayName("행정사 경로를 가진 외국인 신청서 조회 시, 에러 없이 CloudFront URL을 반환해야 한다")
    void getLatestForm_WithAgentPathKey_ShouldReturnCloudFrontUrl() {
        // given
        User foreignerUser = userTestFixture.createUser("test@test.com", UserType.FILLED_FOREIGNER);
        ForeignerProfile profile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        String agentPathKey = "agent-profile/test-image.webp";
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(null, profile, null, true);
        ReflectionTestUtils.setField(form, "profileObjectKey", agentPathKey);
        applicationFormRepository.saveAndFlush(form);

        // when
        ApplicationFormDetailResponse response = applicationFormSearchService.getLatestApplicationFormForForeigner(foreignerUser.getEmail());

        // then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.applicationFormId()).isEqualTo(form.getId()),
                () -> assertThat(response.foreignerProfileImgUrl())
                        .isNotNull()
                        .contains("test-cloudfront-domain")
                        .contains(agentPathKey),
                () -> assertThat(response.chatRoomId()).isNull()
        );
    }
}
