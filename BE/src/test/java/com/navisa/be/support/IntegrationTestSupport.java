package com.navisa.be.support;

import com.navisa.be.support.config.MockAwsConfig;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Import(MockAwsConfig.class)
@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

    @ServiceConnection
    static final PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("pgvector/pgvector:pg17")
            .withInitScript("init-postgres.sql");

    @ServiceConnection(name = "redis")
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static {
        postgresDB.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    DatabaseCleaner databaseCleaner;

    @AfterEach
    void cleanupDatabase() {
        databaseCleaner.execute();
    }
}
