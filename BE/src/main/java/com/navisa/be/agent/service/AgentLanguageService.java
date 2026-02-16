package com.navisa.be.agent.service;

import com.navisa.be.agent.exception.AgentException;
import com.navisa.be.agent.model.entity.AgentLanguage;
import com.navisa.be.agent.model.entity.AgentProfile;
import com.navisa.be.agent.repository.AgentLanguageRepository;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.service.LanguageService;
import com.navisa.be.global.web.response.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AgentLanguageService {

    private final LanguageService languageService;
    private final AgentLanguageRepository agentLanguageRepository;

    @Transactional
    public void save(List<Long> languageIds, AgentProfile agentProfile) {
        languageIds = languageIds.stream().distinct().toList();
        checkAllLanguageExists(languageIds);

        // 모두 조회해서 연관관계를 저장
        List<Language> languages = languageService.findAllById(languageIds);
        List<AgentLanguage> agentLanguages = languages.stream()
                .map(language -> new AgentLanguage(agentProfile, language))
                .toList();
        List<AgentLanguage> savedLanguages = agentLanguageRepository.saveAll(agentLanguages);

        agentProfile.addLanguages(savedLanguages);
    }

    private void checkAllLanguageExists(List<Long> languageIds) {
        long requestCount = languageIds.stream().count();
        long foundCount = languageService.countByIdIn(languageIds);
        if (requestCount != foundCount) {
            throw new AgentException(ResponseStatus.INVALID_LANGUAGE);
        }
    }
}
