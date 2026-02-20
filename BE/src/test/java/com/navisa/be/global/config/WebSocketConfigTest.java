package com.navisa.be.global.config;

import com.navisa.be.support.WebSocketIntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;

import static org.assertj.core.api.Assertions.assertThat;

class WebSocketConfigTest extends WebSocketIntegrationTestSupport {

    @Autowired(required = false)
    private SimpleBrokerMessageHandler simpleBrokerMessageHandler;

    @Test
    @DisplayName("서버의 좀비 세션 방지를 위해 Heartbeat가 설정되어야 한다")
    void shouldHaveHeartbeatConfigured() {
        assertThat(simpleBrokerMessageHandler)
                .as("SimpleBrokerMessageHandler 빈이 등록되어 있어야 합니다.")
                .isNotNull();

        long[] heartbeat = simpleBrokerMessageHandler.getHeartbeatValue();

        assertThat(heartbeat)
                .as("Heartbeat 설정이 null이 아니어야 합니다.")
                .isNotNull();

        assertThat(heartbeat[0])
                .as("서버에서 클라이언트로 보내는 Heartbeat 주기가 0보다 커야 합니다.")
                .isGreaterThan(0);
        assertThat(heartbeat[1])
                .as("클라이언트에서 서버로 보내는 Heartbeat 주기가 0보다 커야 합니다.")
                .isGreaterThan(0);

        assertThat(simpleBrokerMessageHandler.getTaskScheduler())
                .as("Heartbeat 스케줄링을 위한 TaskScheduler가 등록되어 있어야 합니다.")
                .isNotNull();
    }
}
