package com.navisa.be.info.service;

import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.info.dto.LanguageDto;
import com.navisa.be.info.dto.response.GetLanguageListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LanguageService {

    private final LanguageRepository languageRepository;

    public GetLanguageListResponse getLanguageList() {
        List<Language> entities = languageRepository.findAllByOrderByIdAsc();
        List<LanguageDto> dtoList = entities.stream()
                .map(entity -> new LanguageDto(entity.getId(), entity.getValue()))
                .toList();
        return new GetLanguageListResponse(dtoList);
    }
}
