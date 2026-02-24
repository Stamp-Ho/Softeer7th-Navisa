package com.navisa.be.application.scheduler;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.service.ChatRoomQueryService;
import com.navisa.be.chat.service.ChatServiceFacade;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ForeignerFeedbackRequestSchedulerTest {

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ApplicationFormRepository applicationFormRepository;

    @Mock
    private ChatRoomQueryService chatRoomQueryService;

    @Mock
    private ChatServiceFacade chatServiceFacade;

    @InjectMocks
    private ForeignerFeedbackRequestScheduler scheduler;

    @Test
    @DisplayName("17일 지난 미완료 신청서가 있을 경우 알림 메시지를 전송한다")
    void publishRequestMessages_success() {
        // given
        UUID agentUserId = UUID.randomUUID();
        Long chatRoomId = 1L;

        ForeignerProfile foreignerProfile = mock(ForeignerProfile.class);

        AgentProfile agentProfile = mock(AgentProfile.class);
        given(agentProfile.getUserId()).willReturn(agentUserId);

        ApplicationForm applicationForm = mock(ApplicationForm.class);
        given(applicationForm.getForeignerProfile()).willReturn(foreignerProfile);
        given(applicationForm.getAgentProfile()).willReturn(agentProfile);

        ChatRoom chatRoom = mock(ChatRoom.class);
        given(chatRoom.getId()).willReturn(chatRoomId);

        given(applicationFormRepository.findApplicationFormsRequiringFeedback(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(applicationForm));

        // executeWithoutResult 내부의 콜백을 즉시 실행하도록 설정
        doAnswer(invocation -> {
            Consumer<TransactionStatus> callback = invocation.getArgument(0);
            callback.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        given(chatRoomQueryService.findByAgentProfileAndForeignerProfile(agentProfile, foreignerProfile))
                .willReturn(chatRoom);

        // when
        scheduler.publishRequestMessages();

        // then
        verify(applicationFormRepository).findApplicationFormsRequiringFeedback(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(chatRoomQueryService).findByAgentProfileAndForeignerProfile(agentProfile, foreignerProfile);
        verify(chatServiceFacade).saveAndPublishChatMessage(eq(agentUserId), any(ChatMessageRequest.class), eq(chatRoom));
    }

    @Test
    @DisplayName("대상 신청서가 없으면 아무 동작도 하지 않는다")
    void publishRequestMessages_noData() {
        // given
        given(applicationFormRepository.findApplicationFormsRequiringFeedback(any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(Collections.emptyList());

        // when
        scheduler.publishRequestMessages();

        // then
        verify(applicationFormRepository).findApplicationFormsRequiringFeedback(any(LocalDateTime.class), any(LocalDateTime.class));
        verify(chatRoomQueryService, times(0)).findByAgentProfileAndForeignerProfile(any(), any());
        verify(chatServiceFacade, times(0)).saveAndPublishChatMessage(any(), any(), any());
    }
}
