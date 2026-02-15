package com.navisa.be.auth.service;

import com.navisa.be.auth.exception.AuthException;
import com.navisa.be.global.web.response.ResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class GoogleOAuthService {

    private final WebClient webClient = WebClient.create();
    private final String googleClientId;

    private static final String GOOGLE_ID_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    public GoogleOAuthService(@Value("${google.oauth.client-id}") String googleClientId) {
        this.googleClientId = googleClientId;
    }

    public String getGoogleEmail(String idToken) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(GOOGLE_ID_TOKEN_INFO_URL + "?id_token=" + idToken)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.containsKey("error") || !response.containsKey("email")) {
                throw new AuthException(ResponseStatus.GOOGLE_AUTH_ERROR);
            }

            // 이메일 인증 여부 확인
            if (!"true".equals(String.valueOf(response.get("email_verified")))) {
                throw new AuthException(ResponseStatus.GOOGLE_AUTH_ERROR);
            }

            // 토큰 검증
            String audience = (String) response.get("aud");
            if (audience == null || !audience.equals(googleClientId)) {
                throw new AuthException(ResponseStatus.GOOGLE_AUTH_ERROR);
            }

            return (String) response.get("email");

        } catch (Exception e) {
            throw new AuthException(ResponseStatus.GOOGLE_AUTH_ERROR);
        }
    }
}
