package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.dto.response.ApplicationFormIdResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.dto.message.ChatMessageRequest;
import com.navisa.be.chat.dto.message.ChatMessageResponse;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.MessageType;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.service.ChatRoomCrudService;
import com.navisa.be.chat.service.ChatServiceFacade;
import com.navisa.be.chat.service.ProposalCrudService;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationFormForAgentService {

    private final UserCrudService userCrudService;
    private final AgentProfileCrudService agentProfileCrudService;
    private final ApplicationFormRepository applicationFormRepository;
    private final ProposalCrudService proposalCrudService;
    private final ChatRoomCrudService chatRoomCrudService;
    private final ChatServiceFacade chatServiceFacade;

    @Transactional
    public ApplicationFormIdResponse updateApplicationStatus(String email, UUID formId, Boolean isDone) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateAgentOwnership(user, form);

        if(isDone && form.getExportedAt() == null){
            form.updateStatus(isDone);
            applicationFormRepository.saveAndFlush(form);
            ChatRoom chatRoom = chatRoomCrudService.findByAgentIdAndForeignerId(form.getAgentProfile().getId(), form.getForeignerProfile().getId());
            ChatMessageResponse response = ChatMessageResponse.createReviewRequiredEventMessage(form.getForeignerProfile().getUserId(), chatRoom.getId());
            chatServiceFacade.publishEventMessage(form.getForeignerProfile().getUserId(), response);
        }
        else{
            form.updateStatus(isDone);
        }

        return new ApplicationFormIdResponse(form.getId(), form.getUpdatedAt());
    }

    @Transactional
    public ApplicationFormFinishedStatusResponse finishApplication(String email, UUID formId, Boolean isFinished) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm currentForm = applicationFormRepository.findWithAgentProfileAndForeignerProfileById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateAgentOwnership(user, currentForm);

        if (!isFinished) {
            throw new ApplicationFormException(ResponseStatus.FINISH_CONDITION_NOT_MET, "isFinished는 true여야 상태 반영이 가능합니다.");
        }

        currentForm.updateFinish(isFinished);
        applicationFormRepository.saveAndFlush(currentForm);
        updateRelatedProposalToCompleted(currentForm);

        // 신청서 복사본 생성
        ApplicationForm nextForm = ApplicationForm.createRenewalForm(currentForm);
        ApplicationForm savedNextForm = applicationFormRepository.save(nextForm);

        ChatRoom chatRoom = chatRoomCrudService
                .findByAgentIdAndForeignerId(
                        currentForm.getAgentProfile().getId(),
                        currentForm.getForeignerProfile().getId());

        // 피드백 요청 메세지 생성
        ChatMessageRequest chatRequest = new ChatMessageRequest(
                chatRoom.getId(),
                null,
                "수임이 종료되었습니다. 해당 수임 계약에 대한 피드백 작성 부탁드립니다.",
                MessageType.FEEDBACK_REQUIRED
        );
        chatServiceFacade.saveAndPublishChatMessage(currentForm.getAgentProfile().getUserId(), chatRequest, chatRoom);

        return new ApplicationFormFinishedStatusResponse(
                currentForm.getId(),
                savedNextForm.getId(),
                currentForm.getUpdatedAt());
    }

    @Transactional
    public void updateMailSentTime(UUID formId) {
        ApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));
        form.recordMailSentTime();
    }

    @Transactional
    public void updateAgentProfileConnection(ChatRoom chatRoom) {
        applicationFormRepository.findFirstByAgentProfileIdAndForeignerProfileIdOrderByCreatedAtDesc(
                chatRoom.getAgentProfile().getId(), chatRoom.getForeignerProfile().getId())
                .ifPresent(form -> form.setAgentProfile(null));
    }

    private void validateAgentOwnership(User user, ApplicationForm form) {
        AgentProfile agentProfile = agentProfileCrudService.findByUserId(user.getId());

        if (form.getAgentProfile() == null || !form.getAgentProfile().getId().equals(agentProfile.getId())) {
            throw new ApplicationFormException(ResponseStatus.FORBIDDEN_ACCESS);
        }
    }

    private void updateRelatedProposalToCompleted(ApplicationForm form) {
        if (form.getAgentProfile() != null && form.getForeignerProfile() != null) {
            chatRoomCrudService.findOptionalByAgentIdAndForeignerId(
                    form.getAgentProfile().getId(),
                    form.getForeignerProfile().getId()).flatMap(proposalCrudService::findFirstByChatRoomOrderByIdDesc)
                    .ifPresent(proposal -> {
                        proposal.updateStatus(ProposalStatus.COMPLETED);
                        log.info("제안서 완료 처리 성공: Proposal ID = {}", proposal.getId());
                    });
        }
    }
}
