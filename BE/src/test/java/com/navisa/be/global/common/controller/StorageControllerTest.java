package com.navisa.be.global.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navisa.be.auth.jwt.JwtProvider;
import com.navisa.be.global.common.dto.request.IssuedPresignedUrlRequest;
import com.navisa.be.support.IntegrationTestSupport;
import com.navisa.be.user.model.enums.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StorageControllerTest extends IntegrationTestSupport {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3Presigner s3Presigner; // mock된 빈

    @Autowired
    private JwtProvider jwtProvider;

    @DisplayName("presigned URL 발급에 성공한다")
    @Test
    void issuePresignedUrl_shouldSucceed() throws Exception {
        // given
        IssuedPresignedUrlRequest request = new IssuedPresignedUrlRequest("image/jpeg", "agent-profile");

        //s3Presigner가 가짜 URL을 반환하도록 설정
        PresignedPutObjectRequest mockPresignedRequest = mock(PresignedPutObjectRequest.class);
        try {
            when(mockPresignedRequest.url()).thenReturn(new URL("https://test-bucket.s3.amazonaws.com/test"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(mockPresignedRequest);

        String accessToken = jwtProvider.createAccessToken("email", UUID.randomUUID(), UserType.VALID_AGENT);

        // when & then
        mockMvc.perform(post("/api/storage/presigned-url")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result.url").value("https://test-bucket.s3.amazonaws.com/test"));
    }
}
