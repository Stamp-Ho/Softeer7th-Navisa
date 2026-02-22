package com.navisa.be.global.infra.embedding;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import com.navisa.be.global.web.response.ResponseStatus;
import com.navisa.be.global.web.error.BaseException;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;

@Slf4j
@Component
public class GeminiTextEmbeddingClient implements TextEmbeddingClient {

    private static final String MODEL_ID = "gemini-embedding-001";
    private static final int EMBEDDING_DIMENSION = 512;
    private static final String RETRY_STREAM_KEY = "gemini-embedding-retry-stream";
    private final WebClient webClient;

    @Value("${google.gemini.api.key}")
    private String apiKey;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    public GeminiTextEmbeddingClient(WebClient.Builder webClientBuilder,
                                     @Value("${google.gemini.api.base-url}") String baseUrl) {
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000) // 해외 서버와 커넥션을 맺을 때는 3초가 적당
                                .responseTimeout(Duration.ofSeconds(10)))) // 단건 요청 시 응답시간의 p99를 고려
                .build();
    }

    /**
     * DTO를 사용하여 응답을 처리하도록 개선된 로직
     */
    @CircuitBreaker(name = "geminiEmbedding", fallbackMethod = "fallbackEmbedText")
    public Optional<float[]> embedText(String text, GeminiEmbeddingRequestType taskType, UUID foreignerId) {
        return Optional.ofNullable(callGeminiApi(text, taskType, foreignerId));
    }

    public float[] callGeminiApi(String text, GeminiEmbeddingRequestType taskType, UUID foreignerId) {
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
                    .timeout(Duration.ofMillis(1100)) // 단건 요청 시 응답시간의 p99를 고려
                    .map(GeminiEmbeddingResponse::getFirstVector)
                    .block();
        } catch (BaseException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Embedding Failed: {}", e.getMessage());
            // 예외를 던져서 서킷브레이커가 실패를 감지하도록 함
            throw new BaseException(ResponseStatus.CANNOT_GENERATE_TEXT_EMBEDDING_RESULT);
        }
    }

    public Optional<float[]> fallbackEmbedText(String text, GeminiEmbeddingRequestType taskType, UUID foreignerId, Throwable throwable) {
        log.warn("제미나이 임베딩 실패. 재시도를 위해서 redis streams에 저장. foreignerId: {}, Error: {}", foreignerId, throwable.getMessage());

        // Redis Stream에 메시지 저장
        Map<String, String> message = Map.of(
                "foreignerId", foreignerId.toString(),
                "text", text,
                "taskType", taskType.name());

        try {
            redisTemplate.opsForStream().add(RETRY_STREAM_KEY, message);
        } catch (Exception e) {
            log.error("Redis 저장 실패. 재시도 불가. foreignerId: {}, text: {} Error: {}", foreignerId, text, e.getMessage());
        }

        // 원래 호출부에 빈 배열 반환
        return Optional.empty();
    }

    // 배치 처리를 위한 메서드 Consumer에서 사용.
    @CircuitBreaker(name = "geminiBatchEmbedding", fallbackMethod = "fallbackBatch")
    public Map<String, float[]> embedTextBatch(List<String> texts, GeminiEmbeddingRequestType taskType) {

        String uri = MODEL_ID + ":batchEmbedContents";

        List<Map<String, Object>> requests = texts.stream()
                .map(text -> Map.of(
                        "model", "models/" + MODEL_ID,
                        "content", Map.of("parts", List.of(Map.of("text", text))),
                        "taskType", taskType.getValue(),
                        "output_dimensionality", EMBEDDING_DIMENSION))
                .toList();

        Map<String, Object> body = Map.of("requests", requests);

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path(uri).build())
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(6)) // 배치 요청 시 응답시간의 p99를 고려
                    .block();

            if (response == null || !response.containsKey("embeddings")) {
                throw new BaseException(ResponseStatus.NOT_FOUND_TEXT_EMBEDDING_RESULT);
            }

            LinkedHashMap<String, float[]> result = convertBatchEmbeddingResponseToResult(texts, response);

            return result;
        } catch (BaseException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Batch Embedding Failed: {}", e.getMessage());
            // 예외를 던져서 서킷브레이커가 실패를 감지하도록 함
            throw new BaseException(ResponseStatus.CANNOT_GENERATE_TEXT_EMBEDDING_RESULT);
        }
    }

    private LinkedHashMap<String, float[]> convertBatchEmbeddingResponseToResult(List<String> texts, Map<String, Object> response) {
        // 응답 존재 여부 확인
        List<Map<String, Object>> embeddings = (List<Map<String, Object>>) response.get("embeddings");
        if (embeddings == null || embeddings.isEmpty()) {
            throw new BaseException(ResponseStatus.NOT_FOUND_TEXT_EMBEDDING_RESULT);
        }

        // 개수 일치 여부 확인
        if (embeddings.size() != texts.size()) {
            log.error("Batch Embedding Error: Request size({}) and Response size({}) mismatch!", texts.size(), embeddings.size());
            throw new BaseException(ResponseStatus.CANNOT_GENERATE_TEXT_EMBEDDING_RESULT);
        }

        LinkedHashMap<String, float[]> result = new LinkedHashMap<>();
        for (int i = 0; i < embeddings.size(); i++) {
            Map<String, Object> emb = embeddings.get(i);
            List<Double> values = (List<Double>) emb.get("values");

            // 임베딩 값 유효성 확인
            if (values == null || values.isEmpty() || values.stream().anyMatch(Objects::isNull)) {
                log.error("Empty embedding values found at index: {}", i);
                throw new BaseException(ResponseStatus.CANNOT_GENERATE_TEXT_EMBEDDING_RESULT);
            }

            float[] vector = new float[values.size()];
            for (int j = 0; j < values.size(); j++) {
                Double val = values.get(j);
                vector[j] = val.floatValue();
            }
            result.put(texts.get(i), vector);
        }
        return result;
    }

    // 배치 전용 폴백. 아무것도 하지 않고 빈 맵을 반환하여 다음 주기를 기약
    public Map<String, float[]> fallbackBatch(List<String> texts, GeminiEmbeddingRequestType taskType, Throwable throwable) {
        log.warn("배치 임베딩 실패. 다음 주기에 재시도. Error : {}", throwable.getMessage());
        return Map.of();
    }
}
