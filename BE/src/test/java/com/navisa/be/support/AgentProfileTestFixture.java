package com.navisa.be.support;

import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import com.navisa.be.agent.model.entity.AgentSpecializedJobSummary;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.agent.repository.AgentProfileRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobRepository;
import com.navisa.be.common.model.entity.JobCode;
import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.repository.JobCodeRepository;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.info.model.entity.JobGroup;
import com.navisa.be.info.repository.JobGroupRepository;
import com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository;
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
    private final com.navisa.be.info.repository.JobGroupRepository jobGroupRepository;
    private final com.navisa.be.agent.repository.AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;

    public AgentProfileTestFixture(AgentProfileRepository agentProfileRepository,
            AgentSpecializedJobRepository agentSpecializedJobRepository,
            AgentLanguageRepository agentLanguageRepository,
            JobCodeRepository jobCodeRepository,
            LanguageRepository languageRepository,
            JobGroupRepository jobGroupRepository,
            AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository) {

        this.agentProfileRepository = agentProfileRepository;
        this.agentSpecializedJobRepository = agentSpecializedJobRepository;
        this.agentLanguageRepository = agentLanguageRepository;
        this.jobCodeRepository = jobCodeRepository;
        this.languageRepository = languageRepository;
        this.jobGroupRepository = jobGroupRepository;
        this.agentSpecializedJobSummaryRepository = agentSpecializedJobSummaryRepository;
    }

    public JobCode createJobCode(String code, String name) {
        JobCode jobCode = new JobCode(null, code, name, new float[512], null);
        return jobCodeRepository.save(jobCode);
    }

    public JobCode createJobCode(String code, String name, JobGroup jobGroup) {
        JobCode jobCode = new JobCode(null, code, name, new float[512], jobGroup);
        return jobCodeRepository.save(jobCode);
    }

    public JobGroup createJobGroup(String name) {
        JobGroup jobGroup = new JobGroup(null, name,
                new java.util.ArrayList<>());
        return jobGroupRepository.save(jobGroup);
    }

    public AgentSpecializedJobSummary createAgentSpecializedJobSummary(UUID agentId,
            JobCode jobCode) {
        AgentSpecializedJobSummary summary = new AgentSpecializedJobSummary(
                agentId, jobCode);
        return agentSpecializedJobSummaryRepository.save(summary);
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
                "Comment");

        agentProfileRepository.save(profile);

        AgentSpecializedJob agentJob = new AgentSpecializedJob(profile, job);
        agentSpecializedJobRepository.save(agentJob);

        AgentLanguage agentLang = new AgentLanguage(profile, lang);
        agentLanguageRepository.save(agentLang);

        return profile;
    }
}
