// SPDX-License-Identifier: Apache-2.0
package com.jseed.kafka.starter;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Data
@ConfigurationProperties("jseed.kafka")
public class KafkaStarterProperties {
    private boolean enabled;
    private int maxAttempts = 3;
    private long backoffMillis = 1000;
}
