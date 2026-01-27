package com.navisa.be.common.infrastructure.client;

import com.navisa.be.common.model.enums.ResponseStatus;
import com.navisa.be.common.exception.BaseException;
import com.navisa.be.common.dto.response.GeminiEmbeddingResponse;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiTextEmbeddingClient implements TextEmbeddingClient {

    @Value("${google.gemini.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private static final String MODEL_ID = "gemini-embedding-001";

    @Autowired
    public GeminiTextEmbeddingClient(WebClient.Builder webClientBuilder,
                                     @Value("${google.gemini.api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000) // 연결 2초
                                .responseTimeout(Duration.ofSeconds(2))             // 응답 2초
                ))
                .build();
    }

    /**
     * DTO를 사용하여 응답을 처리하도록 개선된 로직
     */
    public List<Double> embedText(String text, GeminiEmbeddingRequestType taskType) {
        String uri = MODEL_ID + ":embedContent";

        Map<String, Object> body = Map.of(
                "model", "models/" + MODEL_ID,
                "content", Map.of("parts", List.of(Map.of("text", text))),
                "taskType", taskType.getValue());

        try {
            return webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(uri).build())
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(GeminiEmbeddingResponse.class)
                    .map(GeminiEmbeddingResponse::getFirstVector)
                    .block();
        } catch (BaseException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(ResponseStatus.CANNOT_GENERATE_TEXT_EMBEDDING_RESULT);
        }
    }
}
