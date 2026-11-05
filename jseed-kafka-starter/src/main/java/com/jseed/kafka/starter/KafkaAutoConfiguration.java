// SPDX-License-Identifier: Apache-2.0
package com.jseed.kafka.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Native KafkaTemplate and @KafkaListener APIs remain the public messaging API.
 *
 * @author cyrus
 */
@AutoConfiguration(
        afterName = "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration")
@EnableConfigurationProperties(KafkaStarterProperties.class)
@ConditionalOnProperty(prefix = "jseed.kafka", name = "enabled", havingValue = "true")
public class KafkaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(CommonErrorHandler.class)
    public DefaultErrorHandler jseedKafkaErrorHandler(
            KafkaOperations<Object, Object> operations, KafkaStarterProperties p) {
        if (p.getMaxAttempts() < 1 || p.getBackoffMillis() < 0)
            throw new IllegalArgumentException("Invalid Kafka retry settings");
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(operations);
        recoverer.setFailIfSendResultIsError(true);
        return new DefaultErrorHandler(
                recoverer, new FixedBackOff(p.getBackoffMillis(), p.getMaxAttempts() - 1));
    }
}
