// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author cyrus
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"kafka", "rabbitmq"})
@Testcontainers
class BrokerDeliveryIT {
    @Container static final KafkaContainer KAFKA = new KafkaContainer("apache/kafka-native:4.0.0");

    @Container
    static final GenericContainer<?> RABBIT =
            new GenericContainer<>("rabbitmq:4")
                    .withEnv("RABBITMQ_DEFAULT_USER", "jseed")
                    .withEnv("RABBITMQ_DEFAULT_PASS", "integration-only")
                    .withExposedPorts(5672);

    @DynamicPropertySource
    static void serviceAddresses(DynamicPropertyRegistry properties) {
        properties.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        properties.add("jseed.kafka.backoff-millis", () -> 10);
        properties.add("spring.rabbitmq.host", RABBIT::getHost);
        properties.add("spring.rabbitmq.port", () -> RABBIT.getMappedPort(5672));
        properties.add("spring.rabbitmq.username", () -> "jseed");
        properties.add("spring.rabbitmq.password", () -> "integration-only");
    }

    @Autowired private KafkaTemplate<String, String> kafka;
    @Autowired private RabbitTemplate rabbit;

    @Test
    void sendsExhaustedKafkaRecordsToDeadLetterTopic() throws Exception {
        var configuration =
                Map.<String, Object>of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "jseed-integration-dlt",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        try (var consumer =
                new KafkaConsumer<>(
                        configuration, new StringDeserializer(), new StringDeserializer())) {
            consumer.subscribe(List.of("jseed-demo-dlt"));
            kafka.send("jseed-demo", "fail").get(15, TimeUnit.SECONDS);
            long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
            while (System.nanoTime() < deadline) {
                for (var record : consumer.poll(Duration.ofMillis(500))) {
                    if ("fail".equals(record.value())) {
                        return;
                    }
                }
            }
            throw new AssertionError("Failed Kafka record did not reach the dead-letter topic");
        }
    }

    @Test
    void rejectsExhaustedRabbitMessagesToDeadLetterQueue() {
        rabbit.convertAndSend("", "jseed.demo", "fail");
        assertThat(rabbit.receiveAndConvert("jseed.demo.dead", 15000)).isEqualTo("fail");
    }
}
