package com.navisa.be.application.service;

import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationFinishResponse;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.chat.model.entity.ChatRoom;
import com.navisa.be.chat.model.enums.ProposalStatus;
import com.navisa.be.chat.repository.ChatRoomRepository;
import com.navisa.be.chat.repository.ProposalRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationCommandService {

    private final UserQueryService userQueryService;
    private final ApplicationFormRepository applicationFormRepository;
    private final ProposalRepository proposalRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 비자 신청서 저장 및 수정
    @Transactional
    public VisaApplicationSaveResponse saveVisaForm(String email, UUID visaFormId, VisaApplicationSaveRequest request) {
        User user = userQueryService.findByEmail(email);

        VisaApplicationForm form = applicationFormRepository.findById(visaFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateOwner(user, form);

        form.updateSections(request.sections(), request.totalCount(), request.filledCount());
        applicationFormRepository.saveAndFlush(form); // updatedAt 갱신

        return new VisaApplicationSaveResponse(form.getId(), form.getUpdatedAt());
    }

    // 외국인 증명사진 저장
    @Transactional
    public VisaApplicationSaveResponse saveProfilePhoto(String email, UUID visaFormId, String objectKey) {
        User user = userQueryService.findByEmail(email);

        VisaApplicationForm form = applicationFormRepository.findById(visaFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateOwner(user, form);

        form.updateProfileImage(objectKey);
        applicationFormRepository.saveAndFlush(form);

        return new VisaApplicationSaveResponse(form.getId(), form.getUpdatedAt());
    }

    // 신청서 작성 상태 변경
    @Transactional
    public VisaApplicationSaveResponse updateApplicationStatus(String email, UUID visaFormId, Boolean isDone) {
        User user = userQueryService.findByEmail(email);

        VisaApplicationForm form = applicationFormRepository.findById(visaFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateOwner(user, form);

        form.updateStatus(isDone);
        applicationFormRepository.saveAndFlush(form);

        return new VisaApplicationSaveResponse(form.getId(), form.getUpdatedAt());
    }

    // 행정사의 수임 종료에 대한 상태 변경
    @Transactional
    public VisaApplicationFinishResponse finishApplication(String email, UUID visaFormId, Boolean isFinished) {
        User user = userQueryService.findByEmail(email);

        VisaApplicationForm currentForm = applicationFormRepository.findById(visaFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateOwner(user, currentForm);

        if (!isFinished) {
            throw new ApplicationException(ResponseStatus.FINISH_CONDITION_NOT_MET);
        }

        currentForm.updateFinish(isFinished);
        updateRelatedProposalToCompleted(currentForm);

        // 신청서 복사본 생성
        VisaApplicationForm nextForm = VisaApplicationForm.createRenewalForm(currentForm);
        VisaApplicationForm savedNextForm = applicationFormRepository.save(nextForm);

        return new VisaApplicationFinishResponse(
                currentForm.getId(),
                savedNextForm.getId(),
                currentForm.getUpdatedAt()
        );
    }

    // 외국인의 수임 종료 요청
    @Transactional
    public VisaApplicationFinishResponse finishByForeigner(String email) {
        User user = userQueryService.findByEmail(email);

        VisaApplicationForm latestForm = applicationFormRepository
                .findFirstByForeignerProfile_UserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        // 이미 종료된 건인지 확인
        if (latestForm.isFinished()) {
            throw new ApplicationException(ResponseStatus.ALREADY_FINISHED);
        }

        // 메일 발송 후 3일 경과 여부
        if (latestForm.getMailSentAt() == null ||
                latestForm.getMailSentAt().isAfter(LocalDateTime.now().minusDays(3))) {
            throw new ApplicationException(ResponseStatus.FINISH_CONDITION_NOT_MET);
        }

        latestForm.updateFinish(true);
        updateRelatedProposalToCompleted(latestForm);
        VisaApplicationForm nextForm = VisaApplicationForm.createRenewalForm(latestForm);
        VisaApplicationForm savedNextForm = applicationFormRepository.save(nextForm);

        return new VisaApplicationFinishResponse(
                latestForm.getId(),
                savedNextForm.getId(),
                latestForm.getUpdatedAt()
        );
    }

    private void validateOwner(User user, VisaApplicationForm form) {
        UUID loginUserId = user.getId();

        // 1. 행정사인 경우: 해당 서류의 담당 행정사인지 확인
        if (user.getUserType() == UserType.VALID_AGENT) {
            if (!form.getAgentProfile().getUserId().equals(loginUserId)) {
                throw new ApplicationException(ResponseStatus.FORBIDDEN_ACCESS);
            }
            return;
        }

        // 2. 외국인인 경우: 해당 서류의 주인인 외국인인지 확인
        if (user.getUserType() == UserType.UNFILLED_FOREIGNER || user.getUserType() == UserType.FILLED_FOREIGNER) {
            if (!form.getForeignerProfile().getUserId().equals(loginUserId)) {
                throw new ApplicationException(ResponseStatus.FORBIDDEN_ACCESS);
            }
            return;
        }

        throw new ApplicationException(ResponseStatus.FORBIDDEN_ACCESS);
    }

    // 제안 상태 변경
    private void updateRelatedProposalToCompleted(VisaApplicationForm form) {
        if (form.getAgentProfile() != null && form.getForeignerProfile() != null) {
            chatRoomRepository.findByAgentIdAndForeignerId(
                    form.getAgentProfile().getId(),
                    form.getForeignerProfile().getId()
            ).ifPresent(chatRoom -> {
                proposalRepository.findFirstByChatRoomOrderByIdDesc(chatRoom)
                        .ifPresent(proposal -> {
                            proposal.updateStatus(ProposalStatus.COMPLETED);
                            log.info("제안서 완료 처리 성공: Proposal ID = {}", proposal.getId());
                        });
            });
        }
    }

    // 메일 발송 시간 기록
    @Transactional
    public void updateMailSentTime(UUID formId) {
        VisaApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));
        form.recordMailSentTime();
    }

    public void updateAgentProfileConnection(ChatRoom chatRoom) {
        applicationFormRepository.findFirstByAgentProfileIdAndForeignerProfileIdOrderByCreatedAtDesc(
                chatRoom.getAgentProfile().getId(), chatRoom.getForeignerProfile().getId())
                .ifPresent(form -> form.setAgentProfile(null));
    }

    @Transactional(readOnly = true)
    public boolean existsByForeignerProfileIdAndAgentProfileIdAndIsFinishedTrue(UUID foreignerProfileId, UUID agentProfileId) {
        return applicationFormRepository.existsByForeignerProfileIdAndAgentProfileIdAndIsFinishedTrue(
                foreignerProfileId,
                agentProfileId
        );
    }
}
