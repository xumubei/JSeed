// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.websocket.starter.WebSocketGateway;
import com.jseed.websocket.starter.WebSocketMessageEvent;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

/**
 * @author cyrus
 */
@Profile("websocket")
@RestController
@RequestMapping("/demo/websocket")
public class WebSocketDemo {
    private final WebSocketGateway gateway;

    public WebSocketDemo(WebSocketGateway gateway) {
        this.gateway = gateway;
    }

    @EventListener
    public void echo(WebSocketMessageEvent event) {
        gateway.send(event.sessionId(), event.payload());
    }

    @PostMapping("/broadcast")
    public int broadcast(@RequestBody String payload) {
        return gateway.broadcast(payload);
    }
}
