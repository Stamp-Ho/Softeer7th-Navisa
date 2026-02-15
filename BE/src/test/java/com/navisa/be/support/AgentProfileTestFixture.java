package com.navisa.be.support;

import com.navisa.be.agent.model.entity.*;
import com.navisa.be.agent.repository.*;
import com.navisa.be.agent.model.enums.BadgeName;
import com.navisa.be.global.common.model.entity.JobCode;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.model.entity.Nationality;
import com.navisa.be.global.common.repository.JobCodeRepository;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.global.common.repository.NationalityRepository;
import com.navisa.be.global.common.model.entity.JobGroup;
import com.navisa.be.global.common.repository.JobGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Transactional
public class AgentProfileTestFixture {

    private final AgentProfileRepository agentProfileRepository;
    private final AgentSpecializedJobRepository agentSpecializedJobRepository;
    private final AgentLanguageRepository agentLanguageRepository;
    private final JobCodeRepository jobCodeRepository;
    private final LanguageRepository languageRepository;
    private final JobGroupRepository jobGroupRepository;
    private final AgentSpecializedJobSummaryRepository agentSpecializedJobSummaryRepository;
    private final BadgeRepository badgeRepository;
    private final AgentBadgeRepository agentBadgeRepository;
    private final AgentBadgeSummaryRepository agentBadgeSummaryRepository;
    private final AgentReviewRepository agentReviewRepository;
    private final NationalityRepository nationalityRepository;

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

    public Nationality createNationality(String name) {
        Nationality nationality = new Nationality(null, name);
        return nationalityRepository.save(nationality);
    }

    public AgentProfile createAgentProfile(String name, String address, JobCode job, Language lang) {
        String uniqueLicense = UUID.randomUUID().toString().substring(0, 10);
        AgentProfile profile = new AgentProfile(
                name,
                LocalDate.of(1990, 1, 1),
                "origin/profile.url",
                "09:00-18:00",
                "Office Name",
                address,
                "Detail Address",
                "History",
                "010-1234-1234",
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

    public AgentProfile createAgentProfile(String name, String address, UUID userId) {
        String uniqueLicense = UUID.randomUUID().toString().substring(0, 10);
        AgentProfile profile = new AgentProfile(
                name,
                LocalDate.of(1990, 1, 1),
                "origin/profile.url",
                "09:00-18:00",
                "Office Name",
                address,
                "Detail Address",
                "History",
                "010-1234-1234",
                userId,
                uniqueLicense,
                LocalDate.now(),
                "inner",
                "mgmt",
                "Comment");
        return agentProfileRepository.save(profile);
    }

    public AgentSpecializedJob createAgentSpecializedJob(AgentProfile agentProfile, JobCode jobCode) {
        AgentSpecializedJob agentJob = new AgentSpecializedJob(agentProfile, jobCode);
        agentProfile.addSpecializedJobCodes(List.of(agentJob));
        return agentSpecializedJobRepository.save(agentJob);
    }

    public AgentLanguage createAgentLanguage(AgentProfile agentProfile, Language language) {
        AgentLanguage agentLang = new AgentLanguage(agentProfile, language);
        agentProfile.addLanguages(List.of(agentLang));
        return agentLanguageRepository.save(agentLang);
    }

    public Badge createBadge(BadgeName badgeName) {
        Badge badge = new Badge(badgeName);
        return badgeRepository.save(badge);
    }

    public AgentReview createAgentReview(AgentProfile agentProfile, Long proposalId, Badge badge) {
        AgentReview review = new AgentReview(agentProfile.getId(), UUID.randomUUID(), proposalId, "Review Content",
                new double[512]);
        review = agentReviewRepository.save(review);

        AgentBadge agentBadge = new AgentBadge(badge, review);
        agentBadgeRepository.save(agentBadge);

        AgentBadgeSummary badgeSummary = new AgentBadgeSummary(agentProfile.getId(), badge);
        agentBadgeSummaryRepository.save(badgeSummary);

        return review;
    }
}
