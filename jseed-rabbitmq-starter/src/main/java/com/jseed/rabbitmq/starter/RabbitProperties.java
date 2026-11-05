// SPDX-License-Identifier: Apache-2.0
package com.jseed.rabbitmq.starter;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Data
@ConfigurationProperties("jseed.rabbitmq")
public class RabbitProperties {
    private boolean enabled;
    private int prefetch = 20;
    private int concurrentConsumers = 1;
    private int maxAttempts = 3;
}
