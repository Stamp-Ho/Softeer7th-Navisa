package com.navisa.be.application.service;

import com.navisa.be.application.exception.ApplicationFormException;
import com.navisa.be.application.model.entity.ApplicationForm;
import com.navisa.be.application.repository.ApplicationFormRepository;
import com.navisa.be.foreigner.model.entity.ForeignerProfile;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationFormCrudService {

    private final ApplicationFormRepository applicationFormRepository;

    @Transactional
    public ApplicationForm createInitForm(ForeignerProfile foreignerProfile) {
        return applicationFormRepository.save(new ApplicationForm(foreignerProfile));
    }

    @Transactional(readOnly = true)
    public ApplicationForm findCurrentApplicationForm(UUID foreignerId, UUID agentId) {
        return applicationFormRepository.findCurrentAppFormNative(foreignerId, agentId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public ApplicationForm findRecentApplicationFormByForeignerId(UUID foreignerId) {
        return applicationFormRepository.findFirstByForeignerProfile_IdOrderByCreatedAtDesc(foreignerId)
                .orElseThrow(() -> new ApplicationFormException(ResponseStatus.VISA_APP_FORM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<ApplicationForm> findOptionalCurrentApplicationForm(UUID foreignerId, UUID agentId) {
        return applicationFormRepository.findCurrentAppFormNative(foreignerId, agentId);
    }

    @Transactional(readOnly = true)
    public boolean existsByForeignerProfileIdAndAgentProfileIdAndIsFinishedTrue(UUID id, UUID matchedAgentId) {
        return applicationFormRepository.existsByForeignerProfileIdAndAgentProfileIdAndIsFinishedTrue(id, matchedAgentId);
    }
}
