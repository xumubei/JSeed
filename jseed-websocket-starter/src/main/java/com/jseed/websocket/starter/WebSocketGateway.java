// SPDX-License-Identifier: Apache-2.0
package com.jseed.websocket.starter;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serializes writes to each session and bounds pending output.
 *
 * @author cyrus
 */
public class WebSocketGateway extends TextWebSocketHandler implements AutoCloseable {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher events;
    private final WebSocketProperties properties;

    public WebSocketGateway(ApplicationEventPublisher events, WebSocketProperties properties) {
        if (properties.getSendTimeLimitMillis() < 1 || properties.getBufferSizeLimitBytes() < 1)
            throw new IllegalArgumentException("Invalid WebSocket send limits");
        this.events = events;
        this.properties = properties;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(
                session.getId(),
                new ConcurrentWebSocketSessionDecorator(
                        session,
                        properties.getSendTimeLimitMillis(),
                        properties.getBufferSizeLimitBytes()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        events.publishEvent(new WebSocketMessageEvent(session.getId(), message.getPayload()));
    }

    public boolean send(String sessionId, String payload) {
        WebSocketSession session = sessions.get(sessionId);
        if (session == null || !session.isOpen()) {
            sessions.remove(sessionId);
            return false;
        }
        try {
            session.sendMessage(new TextMessage(payload));
            return true;
        } catch (IOException error) {
            sessions.remove(sessionId);
            try {
                session.close(CloseStatus.SERVER_ERROR);
            } catch (IOException closeError) {
                error.addSuppressed(closeError);
            }
            throw new UncheckedIOException(error);
        }
    }

    public int broadcast(String payload) {
        int sent = 0;
        for (String id : sessions.keySet()) if (send(id, payload)) sent++;
        return sent;
    }

    public int sessionCount() {
        return sessions.size();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable error) throws IOException {
        sessions.remove(session.getId());
        session.close(CloseStatus.SERVER_ERROR);
    }

    @Override
    public void close() {
        for (WebSocketSession session : sessions.values())
            try {
                session.close(CloseStatus.GOING_AWAY);
            } catch (IOException ignored) {
                /* Session is already unusable during shutdown. */
            }
        sessions.clear();
    }
}
