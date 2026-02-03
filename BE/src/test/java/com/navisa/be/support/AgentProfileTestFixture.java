package com.navisa.be.support;

import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class AgentProfileTestFixture {

    private final AgentProfileRepository agentProfileRepository;
    private final AgentSpecializedJobRepository agentSpecializedJobRepository;
    private final AgentLanguageRepository agentLanguageRepository;
    private final com.navisa.be.common.repository.JobCodeRepository jobCodeRepository;
    private final com.navisa.be.common.repository.LanguageRepository languageRepository;

    public AgentProfileTestFixture(AgentProfileRepository agentProfileRepository,
            AgentSpecializedJobRepository agentSpecializedJobRepository,
            AgentLanguageRepository agentLanguageRepository,
            com.navisa.be.common.repository.JobCodeRepository jobCodeRepository,
            com.navisa.be.common.repository.LanguageRepository languageRepository) {
        this.agentProfileRepository = agentProfileRepository;
        this.agentSpecializedJobRepository = agentSpecializedJobRepository;
        this.agentLanguageRepository = agentLanguageRepository;
        this.jobCodeRepository = jobCodeRepository;
        this.languageRepository = languageRepository;
    }

    public JobCode createJobCode(String code, String name) {
        JobCode jobCode = new JobCode(null, code, name, new float[512]);
        return jobCodeRepository.save(jobCode);
    }

    public Language createLanguage(String name) {
        Language language = new Language(null, name);
        return languageRepository.save(language);
    }

    public AgentProfile createAgentProfile(String name, String address, JobCode job, Language lang) {
        String uniqueLicense = UUID.randomUUID().toString().substring(0, 10);
        AgentProfile profile = new AgentProfile(
                name,
                LocalDate.of(1990, 1, 1),
                "http://profile.url",
                "09:00-18:00",
                "Office Name",
                address,
                "Detail Address",
                "History",
                UUID.randomUUID(),
                uniqueLicense,
                LocalDate.now(),
                "inner",
                "mgmt",
                "Comment"
        );
        agentProfileRepository.save(profile);

        AgentSpecializedJob agentJob = new AgentSpecializedJob(profile, job);
        agentSpecializedJobRepository.save(agentJob);

        AgentLanguage agentLang = new AgentLanguage(profile, lang);
        agentLanguageRepository.save(agentLang);

        return profile;
    }
}
