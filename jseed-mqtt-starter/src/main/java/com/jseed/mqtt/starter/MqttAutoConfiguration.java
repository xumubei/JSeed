// SPDX-License-Identifier: Apache-2.0
package com.jseed.mqtt.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cyrus
 */
@AutoConfiguration
@EnableConfigurationProperties(MqttProperties.class)
@ConditionalOnProperty(prefix = "jseed.mqtt", name = "enabled", havingValue = "true")
public class MqttAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public MqttGateway mqttGateway(MqttProperties properties) {
        return new MqttGateway(properties);
    }
}
