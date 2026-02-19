package com.navisa.be.application.service;

import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.service.AgentProfileCrudService;
import com.navisa.be.application.dto.response.ApplicationFormFinishedStatusResponse;
import com.navisa.be.application.dto.response.ApplicationFormIdResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.service.ChatRoomQueryService;
import com.navisa.be.chat.service.ProposalService;
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
    private final ProposalService proposalService;
    private final ChatRoomQueryService chatRoomQueryService;

    @Transactional
    public ApplicationFormIdResponse updateApplicationStatus(String email, UUID formId, Boolean isDone) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateAgentOwnership(user, form);

        form.updateStatus(isDone);
        applicationFormRepository.saveAndFlush(form);

        return new ApplicationFormIdResponse(form.getId(), form.getUpdatedAt());
    }

    @Transactional
    public ApplicationFormFinishedStatusResponse finishApplication(String email, UUID formId, Boolean isFinished) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm currentForm = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateAgentOwnership(user, currentForm);

        if (!isFinished) {
            throw new ApplicationFormException(ResponseStatus.FINISH_CONDITION_NOT_MET);
        }

        currentForm.updateFinish(isFinished);
        applicationFormRepository.saveAndFlush(currentForm);
        updateRelatedProposalToCompleted(currentForm);

        // 신청서 복사본 생성
        ApplicationForm nextForm = ApplicationForm.createRenewalForm(currentForm);
        ApplicationForm savedNextForm = applicationFormRepository.save(nextForm);

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
            chatRoomQueryService.findByAgentIdAndForeignerId(
                    form.getAgentProfile().getId(),
                    form.getForeignerProfile().getId()).flatMap(proposalService::findFirstByChatRoomOrderByIdDesc)
                    .ifPresent(proposal -> {
                        proposal.updateStatus(ProposalStatus.COMPLETED);
                        log.info("제안서 완료 처리 성공: Proposal ID = {}", proposal.getId());
                    });
        }
    }
}
