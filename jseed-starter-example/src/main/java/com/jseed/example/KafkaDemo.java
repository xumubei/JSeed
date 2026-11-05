// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author cyrus
 */
@Profile("kafka")
@RestController
@RequestMapping("/demo/kafka")
public class KafkaDemo {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaDemo.class);
    private static final String TOPIC = "jseed-demo";
    private final KafkaTemplate<String, String> kafka;

    public KafkaDemo(KafkaTemplate<String, String> kafka) {
        this.kafka = kafka;
    }

    @Bean
    static NewTopic demoTopic() {
        return TopicBuilder.name(TOPIC).partitions(1).replicas(1).build();
    }

    @Bean
    static NewTopic demoDeadLetterTopic() {
        return TopicBuilder.name(TOPIC + "-dlt").partitions(1).replicas(1).build();
    }

    @PostMapping
    public Map<String, Long> publish(@RequestBody String value) throws Exception {
        var result = kafka.send(TOPIC, value).get(10, TimeUnit.SECONDS);
        return Map.of("offset", result.getRecordMetadata().offset());
    }

    @KafkaListener(topics = TOPIC, groupId = "jseed-demo")
    public void consume(String value) {
        if (value.equals("fail")) {
            throw new IllegalArgumentException("Demo consumer failure");
        }
        LOG.info("Kafka message received, length={}", value.length());
    }
}
