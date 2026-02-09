package com.navisa.be.agent.dto;

import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.model.entity.AgentSpecializedJob;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "행정사 전문분야 dto")
public record AgentDetailExpertiseDto(
        @Schema(description = "행정사 특화직무 id")
        List<Long> jobCodeIds,
        @Schema(description = "행정사 사용가능 언어 id")
        List<Long> languageIds
) {
    public static AgentDetailExpertiseDto entityToDto(AgentProfile agentProfile) {
        List<AgentSpecializedJob> jobCodes = agentProfile.getSpecializedJobs();
        List<AgentLanguage> languages = agentProfile.getLanguages();
        List<Long> jobCodeIds = jobCodes.stream().map(jobCode -> jobCode.getJobCode().getId()).toList();
        List<Long> languageIds = languages.stream().map(lang -> lang.getLanguage().getId()).toList();
        return new AgentDetailExpertiseDto(jobCodeIds, languageIds);
    }
}
