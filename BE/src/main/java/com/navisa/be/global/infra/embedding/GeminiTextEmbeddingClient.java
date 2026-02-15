package com.navisa.be.global.infra.embedding;

import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.error.BaseException;
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
    private static final int EMBEDDING_DIMENSION = 512;

    @Autowired
    public GeminiTextEmbeddingClient(WebClient.Builder webClientBuilder,
                                     @Value("${google.gemini.api.base-url}") String baseUrl) {

        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                                .responseTimeout(Duration.ofSeconds(2))
                ))
                .build();
    }

    /**
     * DTO를 사용하여 응답을 처리하도록 개선된 로직
     */
    public float[] embedText(String text, GeminiEmbeddingRequestType taskType) {
        String uri = MODEL_ID + ":embedContent";

        Map<String, Object> body = Map.of(
                "model", "models/" + MODEL_ID,
                "content", Map.of("parts", List.of(Map.of("text", text))),
                "taskType", taskType.getValue(),
                "output_dimensionality", EMBEDDING_DIMENSION);

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
