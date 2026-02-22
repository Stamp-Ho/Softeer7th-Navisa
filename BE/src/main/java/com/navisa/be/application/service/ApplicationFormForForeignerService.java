package com.navisa.be.application.service;

import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.service.ChatRoomCrudService;
import com.navisa.be.chat.service.ChatRoomQueryService;
import com.navisa.be.chat.service.ChatServiceFacade;
import com.navisa.be.chat.service.ProposalCrudService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationFormForForeignerService {

    private final UserCrudService userCrudService;
    private final ApplicationFormRepository applicationFormRepository;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ProposalCrudService proposalCrudService;
    private final ChatRoomCrudService chatRoomCrudService;
    private final ChatServiceFacade chatServiceFacade;

    @Transactional
    public ApplicationFormFinishedStatusResponse finishByForeigner(String email) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm latestForm = applicationFormRepository
                .findFirstWithAgentProfileAndForeignerProfileByForeignerProfile_UserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        // 이미 종료된 건인지 확인
        if (latestForm.isFinished()) {
            throw new ApplicationFormException(ResponseStatus.ALREADY_FINISHED);
        }

        // 메일 발송 후 3일 경과 여부
        if (latestForm.getMailSentAt() == null ||
                latestForm.getMailSentAt().isAfter(LocalDateTime.now().minusDays(3))) {
            throw new ApplicationFormException(ResponseStatus.FINISH_CONDITION_NOT_MET);
        }

        latestForm.updateFinish(true);
        applicationFormRepository.saveAndFlush(latestForm);
        updateRelatedProposalToCompleted(latestForm);

        ApplicationForm nextForm = ApplicationForm.createRenewalForm(latestForm);
        ApplicationForm savedNextForm = applicationFormRepository.save(nextForm);


        ChatRoom chatRoom = chatRoomCrudService
                .findByAgentIdAndForeignerId(
                        latestForm.getAgentProfile().getId(),
                        latestForm.getForeignerProfile().getId());

        // 피드백 요청 메세지 생성
        ChatMessageRequest chatRequest = new ChatMessageRequest(
                chatRoom.getId(),
                null,
                "수임이 종료되었습니다. 해당 수임 계약에 대한 피드백 작성 부탁드립니다.",
                MessageType.FEEDBACK_REQUIRED
        );
        chatServiceFacade.saveAndPublishChatMessage(latestForm.getAgentProfile().getUserId(), chatRequest, chatRoom);

        return new ApplicationFormFinishedStatusResponse(
                latestForm.getId(),
                savedNextForm.getId(),
                latestForm.getUpdatedAt());
    }

    private void updateRelatedProposalToCompleted(ApplicationForm form) {
        if (form.getAgentProfile() != null && form.getForeignerProfile() != null) {
            chatRoomQueryService.findByAgentIdAndForeignerId(
                    form.getAgentProfile().getId(),
                    form.getForeignerProfile().getId()).flatMap(proposalCrudService::findFirstByChatRoomOrderByIdDesc)
                    .ifPresent(proposal -> {
                        proposal.updateStatus(ProposalStatus.COMPLETED);
                        log.info("제안서 완료 처리 성공: Proposal ID = {}", proposal.getId());
                    });
        }
    }
}
