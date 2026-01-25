package com.navisa.be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class BeApplicationTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresDB = new PostgreSQLContainer<>("postgres:17.7");

    @Test
    void contextLoads() {
    }

}
