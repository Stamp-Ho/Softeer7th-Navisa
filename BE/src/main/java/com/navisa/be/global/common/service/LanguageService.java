package com.navisa.be.global.common.service;

import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.global.common.dto.response.LanguageListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public LanguageListResponse getLanguageList() {
        List<Language> entities = languageRepository.findAllByOrderByIdAsc();
        List<LanguageListResponse.LanguageDto> dtoList = entities.stream()
                .map(entity -> new LanguageListResponse.LanguageDto(entity.getId(), entity.getValue()))
                .toList();
        return new LanguageListResponse(dtoList);
    }
}
