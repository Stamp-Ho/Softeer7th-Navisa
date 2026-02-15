package com.navisa.be.common.infrastructure.client;

import com.navisa.be.global.web.error.BaseException;
import com.navisa.be.global.infra.embedding.GeminiEmbeddingRequestType;
import com.navisa.be.global.infra.embedding.GeminiTextEmbeddingClient;
import com.navisa.be.global.web.response.ResponseStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeminiTextEmbeddingClientTest {

    private MockWebServer mockWebServer;
    private GeminiTextEmbeddingClient geminiClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // WebClient.Builder 생성
        WebClient.Builder builder = WebClient.builder();

        // MockWebServer URL 가져오기
        String mockUrl = mockWebServer.url("/").toString();

        // 생성자에 Mock URL 전달
        geminiClient = new GeminiTextEmbeddingClient(builder, mockUrl);

        // @Value 필드 수동 주입
        ReflectionTestUtils.setField(geminiClient, "apiKey", "test-api-key");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("성공: 텍스트를 전달하면 임베딩 벡터 리스트를 반환한다 (DOCUMENT 타입)")
    void embedText_Success() {
        // given: Mock 응답 설정 (GeminiEmbeddingResponse 구조에 맞춤)
        // GeminiEmbeddingResponse는 record이며, embedding 필드 혹은 embeddings 필드를 가진다.
        // 여기서는 embedding 필드를 사용하는 케이스로 가정.
        String mockResponseJson = """
                {
                    "embedding": {
                        "values": [0.123, 0.456, 0.789]
                    }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        // when
        float[] result = geminiClient.embedText("test text", GeminiEmbeddingRequestType.DOCUMENT);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result[0]).isEqualTo(0.123f);
        assertThat(result[1]).isEqualTo(0.456f);
        assertThat(result[2]).isEqualTo(0.789f);
    }

    @Test
    @DisplayName("성공: 텍스트를 전달하면 임베딩 벡터 리스트를 반환한다 (QUERY 타입, embeddings 필드 사용)")
    void embedText_Success_WithEmbeddingsField() {
        // given: embeddings (List) 필드가 오는 케이스도 처리 가능한지 확인
        String mockResponseJson = """
                {
                    "embeddings": [
                        {
                            "values": [0.987, 0.654, 0.321]
                        }
                    ]
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        // when
        float[] result = geminiClient.embedText("search query", GeminiEmbeddingRequestType.QUERY);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result[0]).isEqualTo(0.987f);
    }

    @Test
    @DisplayName("실패: API 호출 중 500 에러 발생 시 BaseException을 던진다")
    void embedText_Exception() {
        // given: 500 에러 응답 설정
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // when & then
        assertThatThrownBy(() -> geminiClient.embedText("error text", GeminiEmbeddingRequestType.DOCUMENT))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.CANNOT_GENERATE_TEXT_EMBEDDING_RESULT);
    }

    @Test
    @DisplayName("실패: 응답값에 임베딩 벡터가 없는 경우 BaseException(NOT_FOUND_TEXT_EMBEDDING_RESULT)을 던진다")
    void embedText_NotFoundResult() {
        // given: 유효하지만 벡터값이 없는 응답
        String mockResponseJson = "{}";

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponseJson)
                .addHeader("Content-Type", "application/json"));

        // when & then
        assertThatThrownBy(() -> geminiClient.embedText("test text", GeminiEmbeddingRequestType.DOCUMENT))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(ResponseStatus.NOT_FOUND_TEXT_EMBEDDING_RESULT);
    }

}
