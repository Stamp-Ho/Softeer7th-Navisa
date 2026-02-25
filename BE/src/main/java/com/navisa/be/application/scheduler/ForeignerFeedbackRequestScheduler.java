package com.navisa.be.application.scheduler;

import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.service.ChatRoomSearchService;
import com.navisa.be.chat.service.ChatIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ForeignerFeedbackRequestScheduler {

    private final TransactionTemplate transactionTemplate;
    private final ApplicationFormRepository applicationFormRepository;
    private final ChatRoomSearchService chatRoomSearchService;
    private final ChatIntegrationService chatIntegrationService;

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    @SchedulerLock(
            name = "ForeignerFeedbackRequestScheduler_publishRequestMessages", lockAtMostFor = "10m", lockAtLeastFor = "2m"
    )
    public void publishRequestMessages() {
        log.info("내보내기 17일 후 피드백 요청 메시지 발행 스케줄러 시작");

        LocalDate targetDate = LocalDate.now().minusDays(17);
        List<ApplicationForm> allRequiringEnd = applicationFormRepository.findApplicationFormsRequiringFeedback(targetDate.atStartOfDay(), targetDate.plusDays(1).atStartOfDay());

        log.info("{} 개 조회됨", allRequiringEnd.size());

        for (ApplicationForm form : allRequiringEnd) {
            // 명시적으로 트랜잭션 범위 지정
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    ChatRoom chatRoom = chatRoomSearchService.findByAgentProfileAndForeignerProfile(form.getAgentProfile(), form.getForeignerProfile());
                    // 피드백 요청 메세지 생성
                    ChatMessageRequest chatRequest = new ChatMessageRequest(
                            chatRoom.getId(),
                            null,
                            "수임이 종료되었습니다. 해당 수임 계약에 대한 피드백 작성 부탁드립니다.",
                            MessageType.FEEDBACK_REQUIRED
                    );
                    chatIntegrationService.saveAndPublishChatMessage(form.getAgentProfile().getUserId(), chatRequest, chatRoom);

                } catch (Exception e) {
                    log.error("내보내기 17일 후 피드백 요청 메시지 발행 실패: formId={}", form.getId(), e);
                }
            });
        }
        log.info("내보내기 17일 후 피드백 요청 메시지 발행 스케줄러 완료");
    }
}
