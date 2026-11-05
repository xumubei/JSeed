// SPDX-License-Identifier: Apache-2.0
package com.jseed.websocket.starter;

/**
 * @author cyrus
 */
public record WebSocketMessageEvent(String sessionId, String payload) {}
