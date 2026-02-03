package com.navisa.be.info.service;

import com.navisa.be.common.model.entity.Language;
import com.navisa.be.common.repository.LanguageRepository;
import com.navisa.be.info.dto.response.GetLanguageListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class LanguageServiceTest {

    @InjectMocks
    private LanguageService languageService;

    @Mock
    private LanguageRepository languageRepository;

    @Test
    @DisplayName("언어 목록 조회는 리스트 반환한다")
    void getLanguageList_shouldReturnList() {
        // given
        List<Language> entities = List.of(new Language(1L, "한국어"), new Language(2L, "영어"));
        Mockito.when(languageRepository.findAllByOrderByIdAsc()).thenReturn(entities);

        // when
        GetLanguageListResponse response = languageService.getLanguageList();

        // then
        assertThat(response.languageList()).hasSize(2);
        assertThat(response.languageList().get(0).languageId()).isEqualTo(1L);
        assertThat(response.languageList().get(0).value()).isEqualTo("한국어");
    }

    @Test
    @DisplayName("언어 목록 조회는 빈 리스트를 반환한다")
    void getLanguageList_shouldReturnEmptyList_whenNoLanguageExists() {
        // given
        List<Language> entities = List.of();
        Mockito.when(languageRepository.findAllByOrderByIdAsc()).thenReturn(entities);

        // when
        GetLanguageListResponse response = languageService.getLanguageList();

        // then
        assertThat(response.languageList()).hasSize(0);
    }
}