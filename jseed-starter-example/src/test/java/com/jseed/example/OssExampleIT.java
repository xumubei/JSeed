// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.jseed.oss.starter.OssTemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author cyrus
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("oss")
@Testcontainers
class OssExampleIT {
    @Container
    static final GenericContainer<?> MINIO =
            new GenericContainer<>("quay.io/minio/minio:RELEASE.2025-04-22T22-12-26Z")
                    .withEnv("MINIO_ROOT_USER", "jseed-integration")
                    .withEnv("MINIO_ROOT_PASSWORD", "integration-only")
                    .withCommand("server", "/data")
                    .withExposedPorts(9000)
                    .waitingFor(Wait.forHttp("/minio/health/ready"));

    @DynamicPropertySource
    static void endpoint(DynamicPropertyRegistry properties) {
        properties.add(
                "oss.endpoint",
                () -> "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000));
        properties.add("oss.access-key", () -> "jseed-integration");
        properties.add("oss.secret-key", () -> "integration-only");
    }

    @Autowired private OssTemplate oss;

    @Test
    void uploadsDownloadsAndBatchDeletesAnObject() {
        byte[] payload = "seed".getBytes(StandardCharsets.UTF_8);
        String object = oss.putFile("integration.txt", payload).getName();
        assertThat(oss.downloadFileAsBytes(object)).isEqualTo(payload);
        oss.removeFiles(List.of(object));
        assertThat(oss.isFileExists(object)).isFalse();
    }
}
