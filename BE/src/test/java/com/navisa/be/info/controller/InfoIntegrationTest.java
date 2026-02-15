package com.navisa.be.info.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.common.model.entity.Language;
import com.navisa.be.global.common.repository.LanguageRepository;
import com.navisa.be.support.IntegrationTestSupport;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
class InfoIntegrationTest extends IntegrationTestSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private LanguageRepository languageRepository;

    @Test
    @DisplayName("언어 목록 조회에 성공한다")
    void getLanguageList_shouldSucceed() throws Exception {
        // given
        List<Language> languages = languageRepository.saveAll(List.of(new Language(null, "한국어"), new Language(null, "영어")));

        String accessToken = jwtProvider.createAccessToken("email");

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/info/languages")
                .header("Authorization", "Bearer " + accessToken));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.result.languageList[0].languageId").value(languages.get(0).getId()));
    }
}
