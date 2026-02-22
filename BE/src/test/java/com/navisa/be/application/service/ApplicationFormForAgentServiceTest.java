package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.chat.model.entity.ChatMessage;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.repository.ChatMessageRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class ApplicationFormForAgentServiceTest extends IntegrationTestSupport {

    @Autowired
    private ApplicationFormForAgentService applicationFormForAgentService;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

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

    @Test
    @DisplayName("행정사가 수임 종료 시 신청서 상태가 변경되고, agent의 userId로 FEEDBACK_REQUIRED 메세지가 저장된다.")
    void finishApplication_SavesFeedbackMessage() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "서울", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        JobCode jobCode = agentProfileTestFixture.createJobCode("E7", "특수직");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, true);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        // when
        ApplicationFormFinishedStatusResponse response = applicationFormForAgentService
                .finishApplication(agentUser.getEmail(), form.getId(), true);

        // then - 응답 검증
        assertThat(response).isNotNull();
        assertThat(response.closedVisaFormId()).isEqualTo(form.getId());
        assertThat(response.newVisaFormId()).isNotNull();

        // then - FEEDBACK_REQUIRED 메세지 DB 저장 검증
        List<ChatMessage> messages = chatMessageRepository.findAll();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getMessageType()).isEqualTo(MessageType.FEEDBACK_REQUIRED);
        assertThat(messages.get(0).getContent()).contains("수임이 종료되었습니다");
        // 송신자가 agentProfile.id 임을 검증
        assertThat(messages.get(0).getSenderId()).isEqualTo(agentProfile.getId());
    }

    @Test
    @DisplayName("isFinished가 false이면 ApplicationFormException(FINISH_CONDITION_NOT_MET)이 발생한다.")
    void finishApplication_ThrowsWhenNotFinished() {
        // given
        User agentUser = userTestFixture.createUser("agent2@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner2@test.com", UserType.FILLED_FOREIGNER);

        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("행정사", "부산", agentUser.getId());
        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        JobCode jobCode = agentProfileTestFixture.createJobCode("E8", "기타직");
        ApplicationForm form = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile,
                foreignerProfile, jobCode, false);

        // when & then
        assertThatThrownBy(() -> applicationFormForAgentService.finishApplication(agentUser.getEmail(), form.getId(), false))
                .isInstanceOf(ApplicationFormException.class)
                .hasMessageContaining("isFinished는 true여야 상태 반영이 가능합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 formId로 finishApplication 호출 시 VISA_APP_FORM_NOT_FOUND 예외가 발생한다.")
    void finishApplication_ThrowsWhenFormNotFound() {
        // given
        User agentUser = userTestFixture.createUser("agent3@test.com", UserType.VALID_AGENT);

        // when & then
        assertThatThrownBy(() -> applicationFormForAgentService.finishApplication(agentUser.getEmail(),
                UUID.randomUUID(), true))
                .isInstanceOf(ApplicationFormException.class)
                .hasMessageContaining(ResponseStatus.VISA_APP_FORM_NOT_FOUND.getMessage());
    }
}
