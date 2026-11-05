// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.jseed.mqtt.starter.MqttGateway;
import com.jseed.redis.starter.util.RedisUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author cyrus
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"redis", "mqtt"})
@Testcontainers
class RedisMqttIT {
    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);

    @Container
    static final GenericContainer<?> MQTT =
            new GenericContainer<>("eclipse-mosquitto:2")
                    .withExposedPorts(1883)
                    .withCopyToContainer(
                            Transferable.of("listener 1883\nallow_anonymous true\n"),
                            "/mosquitto/config/mosquitto.conf");

    @DynamicPropertySource
    static void serviceAddresses(DynamicPropertyRegistry properties) {
        properties.add("spring.data.redis.host", REDIS::getHost);
        properties.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        properties.add(
                "jseed.redis.address",
                () -> "redis://" + REDIS.getHost() + ":" + REDIS.getMappedPort(6379));
        properties.add("jseed.mqtt.host", MQTT::getHost);
        properties.add("jseed.mqtt.port", () -> MQTT.getMappedPort(1883));
    }

    @Autowired private RedisUtil redis;

    @Autowired private MqttGateway mqtt;

    @Test
    void writesToRedisAndReadsThroughTheStarter() {
        redis.setCacheObject("jseed:integration", "hello", Duration.ofMinutes(1));
        assertThat(redis.<String>getCacheObject("jseed:integration")).isEqualTo("hello");
        assertThat(redis.getTimeToLive("jseed:integration")).isPositive();
    }

    @Test
    void publishesAndReceivesThroughTheBroker() throws Exception {
        String topic = "jseed/integration";
        CompletableFuture<String> received = new CompletableFuture<>();
        mqtt.subscribe(
                        topic,
                        message ->
                                received.complete(
                                        new String(message.payload(), StandardCharsets.UTF_8)))
                .get(10, TimeUnit.SECONDS);
        mqtt.publish(topic, "hello".getBytes(StandardCharsets.UTF_8)).get(10, TimeUnit.SECONDS);
        assertThat(received.get(10, TimeUnit.SECONDS)).isEqualTo("hello");
    }
}
