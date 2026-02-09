package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.LicenseInfoDto;
import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.user.model.entity.User;
import com.navisa.be.user.model.enums.UserType;
import com.navisa.be.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AgentProfileCommandService {

    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final JobCodeRepository jobCodeRepository;
    private final AgentSpecializedJobRepository agentSpecializedJobRepository;
    private final LanguageRepository languageRepository;
    private final AgentLanguageRepository agentLanguageRepository;

    @Transactional
    public AgentProfile registerAgentProfile(RegisterAgentProfileCommand command) {
        User user = userRepository.findByEmail(command.userEmail())
                .orElseThrow(() -> new AgentException(ResponseStatus.INVALID_USER));

        validateLicenseType(command.licenseInfo());

        if (user.getUserType() != UserType.INVALID_AGENT) {
            throw new AgentException(ResponseStatus.NOT_ALLOWED_TO_REGISTER_AGENT_PROFILE);
        }
        AgentProfile savedProfile = agentProfileRepository.save(command.dtoToEntity(user));

        saveAgentSpecializedJob(command.detailedInfo().specializedJobCodeIdList(), savedProfile);
        saveAgentLanguage(command.detailedInfo().availableLanguageIdList(), savedProfile);

        return savedProfile;
    }

    private void saveAgentLanguage(List<Long> languageIds, AgentProfile savedProfile) {
        languageIds = languageIds.stream().distinct().toList();
        checkAllLanguageExists(languageIds);

        // 모두 조회해서 연관관계를 저장
        List<Language> languages = languageRepository.findAllById(languageIds);
        List<AgentLanguage> agentLanguages = languages.stream()
                .map(language -> new AgentLanguage(savedProfile, language))
                .toList();
        List<AgentLanguage> savedLanguages = agentLanguageRepository.saveAll(agentLanguages);

        savedProfile.addLanguages(savedLanguages);
    }

    private void checkAllLanguageExists(List<Long> languageIds) {
        long requestCount = languageIds.stream().count();
        long foundCount = languageRepository.countByIdIn(languageIds);
        if (requestCount != foundCount) {
            throw new AgentException(ResponseStatus.INVALID_LANGUAGE);
        }
    }

    private void saveAgentSpecializedJob(List<Long> jobCodeIds, AgentProfile savedProfile) {
        jobCodeIds = jobCodeIds.stream().distinct().toList();
        checkAllJobCodeExists(jobCodeIds);

        // 모두 조회해서 연관관계를 저장
        List<JobCode> jobCodes = jobCodeRepository.findAllById(jobCodeIds);
        List<AgentSpecializedJob> specializedJobCodes = jobCodes.stream()
                .map(jobCode -> new AgentSpecializedJob(savedProfile, jobCode))
                .toList();
        List<AgentSpecializedJob> savedCodes = agentSpecializedJobRepository.saveAll(specializedJobCodes);

        savedProfile.addSpecializedJobCodes(savedCodes);
    }

    private void checkAllJobCodeExists(List<Long> jobCodeIds) {
        long requestCount = jobCodeIds.stream().count();
        long foundCount = jobCodeRepository.countByIdIn(jobCodeIds);
        if (requestCount != foundCount) {
            throw new AgentException(ResponseStatus.INVALID_JOB_CODE);
        }
    }

    private void validateLicenseType(LicenseInfoDto licenseInfo) {
        if (isWalletTypeLicense(licenseInfo) == isPaperTypeLicense(licenseInfo)) {
            throw new AgentException(ResponseStatus.AGENT_PROFILE_MUST_CONTAIN_ONE_TYPE_LICENSE_INFO);
        }
    }

    private boolean isPaperTypeLicense(LicenseInfoDto licenseInfo) {
        return licenseInfo.licenseManagementNo() != null;
    }

    private boolean isWalletTypeLicense(LicenseInfoDto licenseInfo) {
        return (licenseInfo.licenseNo() != null
                && licenseInfo.licenseIssuedAt() != null
                && licenseInfo.licenseInnerPageNo() != null);
    }

    // 행정사 로그인 시 활동 날짜 데이터를 갱신
    @Transactional
    public void syncAgentLoginActivity(UUID userId) {
        AgentProfile profile = agentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new AgentException(ResponseStatus.AGENT_NOT_FOUND));

        profile.syncLoginInfo();
    }
}
