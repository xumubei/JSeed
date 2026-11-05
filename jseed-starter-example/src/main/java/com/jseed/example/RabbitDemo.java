// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

/**
 * @author cyrus
 */
@Profile("rabbitmq")
@RestController
@RequestMapping("/demo/rabbitmq")
public class RabbitDemo {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitDemo.class);
    private static final String QUEUE = "jseed.demo";
    private static final String DEAD_LETTER = "jseed.demo.dead";
    private final RabbitTemplate rabbit;

    public RabbitDemo(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Bean
    static Queue demoQueue() {
        return QueueBuilder.durable(QUEUE)
                .deadLetterExchange("")
                .deadLetterRoutingKey(DEAD_LETTER)
                .build();
    }

    @Bean
    static Queue demoDeadLetterQueue() {
        return QueueBuilder.durable(DEAD_LETTER).build();
    }

    @PostMapping
    public void publish(@RequestBody String value) {
        rabbit.convertAndSend("", QUEUE, value);
    }

    @RabbitListener(queues = QUEUE, containerFactory = "jseedRabbitListenerContainerFactory")
    public void consume(String value) {
        if (value.equals("fail")) {
            throw new IllegalArgumentException("Demo consumer failure");
        }
        LOG.info("RabbitMQ message received, length={}", value.length());
    }
}
