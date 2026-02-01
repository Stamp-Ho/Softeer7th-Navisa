package com.navisa.be.agent.service;

import com.navisa.be.agent.dto.request.RegisterAgentProfileCommand;
import com.navisa.be.agent.exception.AgentProfileDomainException;
import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJobCode;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobCodeRepository;
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

@RequiredArgsConstructor
@Service
public class AgentProfileService {

    private final UserRepository userRepository;
    private final AgentProfileRepository agentProfileRepository;
    private final JobCodeRepository jobCodeRepository;
    private final AgentSpecializedJobCodeRepository agentSpecializedJobCodeRepository;
    private final LanguageRepository languageRepository;
    private final AgentLanguageRepository agentLanguageRepository;

    @Transactional
    public AgentProfile registerAgentProfile(RegisterAgentProfileCommand command) {
        User user = userRepository.findByEmail(command.userEmail())
                .orElseThrow(() -> new AgentProfileDomainException(ResponseStatus.INVALID_USER));

        validateCommand(command);

        if(user.getUserType() != UserType.UNVALID_AGENT){
            throw new AgentProfileDomainException(ResponseStatus.NOT_ALLOWED_TO_REGISTER_AGENT_PROFILE);
        }

        AgentProfile agentProfile = dtoToEntity(command, user);
        AgentProfile savedProfile = agentProfileRepository.save(agentProfile);

        List<Long> jobCodeIds = command.detailedInfo().specializedJobCodeIdList()
                .stream()
                .distinct()
                .toList();
        associateJobCode(jobCodeIds, savedProfile);

        List<Long> languageIds = command.detailedInfo().availableLanguageIdList()
                .stream()
                .distinct()
                .toList();
        associateLanguage(languageIds, savedProfile);

        return savedProfile;
    }

    private void associateLanguage(List<Long> languageIds, AgentProfile savedProfile) {
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
            throw new AgentProfileDomainException(ResponseStatus.INVALID_LANGUAGE);
        }
    }

    private void associateJobCode(List<Long> jobCodeIds, AgentProfile savedProfile) {
        checkAllJobCodeExists(jobCodeIds);

        // 모두 조회해서 연관관계를 저장
        List<JobCode> jobCodes = jobCodeRepository.findAllById(jobCodeIds);
        List<AgentSpecializedJobCode> specializedJobCodes = jobCodes.stream()
                .map(jobCode -> new AgentSpecializedJobCode(savedProfile, jobCode))
                .toList();
        List<AgentSpecializedJobCode> savedCodes = agentSpecializedJobCodeRepository.saveAll(specializedJobCodes);
        savedProfile.addSpecializedJobCodes(savedCodes);
    }

    private void checkAllJobCodeExists(List<Long> jobCodeIds) {
        long requestCount = jobCodeIds.stream().count();
        long foundCount = jobCodeRepository.countByIdIn(jobCodeIds);
        if (requestCount != foundCount) {
            throw new AgentProfileDomainException(ResponseStatus.INVALID_JOB_CODE);
        }
    }

    private AgentProfile dtoToEntity(RegisterAgentProfileCommand command, User user) {
        return new AgentProfile(command.basicInfo().agentName(),
                command.basicInfo().birthDate(),
                command.basicInfo().profileImageUrl(),
                command.basicInfo().businessTime(),
                command.basicInfo().officeName(),
                command.basicInfo().officeAddress(),
                command.basicInfo().officeAddressDetail(),
                command.detailedInfo().additionalHistory(),
                user.getId(),
                command.licenseInfo().licenseNo(),
                command.licenseInfo().licenseIssuedAt(),
                command.licenseInfo().licenseInnerPageNo(),
                command.licenseInfo().licenseManagementNo(),
                command.detailedInfo().agentComment());
    }

    private static void validateCommand(RegisterAgentProfileCommand command) {
        boolean hasBasic = (command.licenseInfo().licenseNo() != null
                && command.licenseInfo().licenseIssuedAt() != null
                && command.licenseInfo().licenseInnerPageNo() != null);
        boolean hasManagement = (command.licenseInfo().licenseManagementNo() != null);
        if (hasBasic == hasManagement) {
            throw new AgentProfileDomainException(ResponseStatus.AGENT_PROFILE_MUST_CONTAIN_ONE_TYPE_LICENSE_INFO);
        }
    }
}
