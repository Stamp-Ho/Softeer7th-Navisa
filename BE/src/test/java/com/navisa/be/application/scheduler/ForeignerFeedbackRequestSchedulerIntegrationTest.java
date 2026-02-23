package com.navisa.be.application.scheduler;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ChatRoomStatus;
import com.navisa.be.chat.service.ChatServiceFacade;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Language;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ForeignerFeedbackRequestSchedulerIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ForeignerFeedbackRequestScheduler proposalEndRequestScheduler;

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

    @MockitoBean
    private ChatServiceFacade chatServiceFacade;

    @Test
    @DisplayName("17일 지난 미완료 신청서가 있을 경우 알림 메시지를 전송한다")
    void publishRequestMessages_success() {
        // given
        User agentUser = userTestFixture.createUser("agent@test.com", UserType.VALID_AGENT);
        User foreignerUser = userTestFixture.createUser("foreigner@test.com", UserType.FILLED_FOREIGNER);

        JobCode jobCode = agentProfileTestFixture.createJobCode("D-2", "D-2");
        Language language = agentProfileTestFixture.createLanguage("Korean");
        AgentProfile agentProfile = agentProfileTestFixture.createAgentProfile("Agent Kim", "Seoul",
                agentUser.getId());
        agentProfileTestFixture.createAgentSpecializedJob(agentProfile, jobCode);
        agentProfileTestFixture.createAgentLanguage(agentProfile, language);

        ForeignerProfile foreignerProfile = foreignerProfileTestFixture.createForeignerProfile(foreignerUser);

        chatRoomTestFixture.createChatRoom(foreignerProfile, agentProfile, ChatRoomStatus.DEFAULT);

        ApplicationForm applicationForm = visaApplicationFormTestFixture.createVisaApplicationForm(agentProfile, foreignerProfile, jobCode, true);
        ReflectionTestUtils.setField(applicationForm, "exportedAt", LocalDateTime.now().minusDays(17).withHour(12).withMinute(0).withSecond(0));
        applicationFormRepository.save(applicationForm);

        // when
        proposalEndRequestScheduler.publishRequestMessages();

        // then
        verify(chatServiceFacade, times(1)).saveAndPublishChatMessage(eq(agentUser.getId()), any(ChatMessageRequest.class), any(ChatRoom.class));
    }
}
