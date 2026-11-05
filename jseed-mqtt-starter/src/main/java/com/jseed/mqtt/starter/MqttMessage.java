// SPDX-License-Identifier: Apache-2.0
package com.jseed.mqtt.starter;

/**
 * Immutable message payload.
 *
 * @author cyrus
 */
public record MqttMessage(String topic, byte[] payload) {
    public MqttMessage {
        payload = payload.clone();
    }

    @Override
    public byte[] payload() {
        return payload.clone();
    }
}
