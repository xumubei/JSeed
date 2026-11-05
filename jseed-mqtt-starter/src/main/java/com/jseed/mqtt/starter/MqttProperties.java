// SPDX-License-Identifier: Apache-2.0
package com.jseed.mqtt.starter;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Getter
@Setter
@ConfigurationProperties("jseed.mqtt")
public class MqttProperties {
    private boolean enabled;
    private String host = "localhost";
    private int port = 1883;
    private String clientId = "jseed-" + java.util.UUID.randomUUID();
    private String username;
    private String password;
    private boolean tls;
    private int qos = 1;
    private long timeoutSeconds = 10;
    private boolean version5;
}
