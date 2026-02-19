package com.navisa.be.application.service;

import com.navisa.be.application.dto.request.ApplicationFormSectionDataRequest;
import com.navisa.be.application.dto.response.ApplicationFormIdResponse;
import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.service.UserCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationFormRegistrationService {

    private final UserCrudService userCrudService;
    private final ApplicationFormRepository applicationFormRepository;

    @Transactional
    public ApplicationFormIdResponse saveApplicationForm(String email, UUID formId,
                                                         ApplicationFormSectionDataRequest request) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateOwner(user, form);

        form.updateSections(request.sections(), request.totalCount(), request.filledCount());
        applicationFormRepository.saveAndFlush(form);

        return new ApplicationFormIdResponse(form.getId(), form.getUpdatedAt());
    }

    @Transactional
    public ApplicationFormIdResponse saveProfilePhoto(String email, UUID formId, String objectKey) {
        User user = userCrudService.findByEmail(email);

        ApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));

        validateOwner(user, form);

        form.updateProfileImage(objectKey);
        applicationFormRepository.saveAndFlush(form);

        return new ApplicationFormIdResponse(form.getId(), form.getUpdatedAt());
    }

    private void validateOwner(User user, ApplicationForm form) {
        UUID loginUserId = user.getId();

        // 1. 행정사인 경우: 해당 서류의 담당 행정사인지 확인
        if (user.getUserType() == UserType.VALID_AGENT) {
            if (form.getAgentProfile() == null) {
                throw new ApplicationFormException(ResponseStatus.INVALID_AGENT);
            }

            if (!form.getAgentProfile().getUserId().equals(loginUserId)) {
                throw new ApplicationFormException(ResponseStatus.FORBIDDEN_ACCESS);
            }
            return;
        }

        // 2. 외국인인 경우: 해당 서류의 주인인 외국인인지 확인
        if (user.getUserType() == UserType.FILLED_FOREIGNER) {
            if (form.getForeignerProfile() == null) {
                throw new ApplicationFormException(ResponseStatus.INVALID_FOREIGNER);
            }

            if (!form.getForeignerProfile().getUserId().equals(loginUserId)) {
                throw new ApplicationFormException(ResponseStatus.FORBIDDEN_ACCESS);
            }
            return;
        }

        throw new ApplicationFormException(ResponseStatus.FORBIDDEN_ACCESS);
    }
}
