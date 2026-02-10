package com.navisa.be.application.service;

import com.navisa.be.application.dto.request.VisaApplicationSaveRequest;
import com.navisa.be.application.dto.response.VisaApplicationSaveResponse;
import com.navisa.be.application.exception.ApplicationException;
import com.navisa.be.application.model.entity.VisaApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationCommandService {

    private final UserRepository userRepository;
    private final ApplicationFormRepository applicationFormRepository;

    // 비자 신청서 저장 및 수정
    @Transactional
    public VisaApplicationSaveResponse saveVisaForm(String email, UUID visaFormId, VisaApplicationSaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.USER_INVALID));

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
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.USER_INVALID));

        VisaApplicationForm form = applicationFormRepository.findById(visaFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        form.updateProfileImage(objectKey);
        applicationFormRepository.saveAndFlush(form);

        return new VisaApplicationSaveResponse(form.getId(), form.getUpdatedAt());
    }

    // 신청서 작성 상태 변경
    @Transactional
    public VisaApplicationSaveResponse updateApplicationStatus(String email, UUID visaFormId, Boolean isDone) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.USER_INVALID));

        VisaApplicationForm form = applicationFormRepository.findById(visaFormId)
                .orElseThrow(() -> new ApplicationException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        form.updateStatus(isDone);
        applicationFormRepository.saveAndFlush(form);

        return new VisaApplicationSaveResponse(form.getId(), form.getUpdatedAt());
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
}
