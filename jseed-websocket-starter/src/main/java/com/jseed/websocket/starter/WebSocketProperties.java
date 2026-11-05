// SPDX-License-Identifier: Apache-2.0
package com.jseed.websocket.starter;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Data
@ConfigurationProperties("jseed.websocket")
public class WebSocketProperties {
    private boolean enabled;
    private String path = "/ws/jseed";
    private String[] allowedOrigins = new String[0];
    private int sendTimeLimitMillis = 10000;
    private int bufferSizeLimitBytes = 65536;
}
