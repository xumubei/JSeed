// SPDX-License-Identifier: Apache-2.0
package com.jseed.websocket.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.*;

/**
 * @author cyrus
 */
@AutoConfiguration
@EnableWebSocket
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jseed.websocket", name = "enabled", havingValue = "true")
public class WebSocketAutoConfiguration {
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public WebSocketGateway webSocketGateway(
            ApplicationEventPublisher events, WebSocketProperties properties) {
        return new WebSocketGateway(events, properties);
    }

    @Bean
    public WebSocketConfigurer jseedWebSocketConfigurer(
            WebSocketGateway gateway, WebSocketProperties p) {
        if (!p.getPath().startsWith("/"))
            throw new IllegalArgumentException("WebSocket path must start with /");
        return registry ->
                registry.addHandler(gateway, p.getPath()).setAllowedOrigins(p.getAllowedOrigins());
    }
}
