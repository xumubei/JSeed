// SPDX-License-Identifier: Apache-2.0
package com.jseed.mqtt.starter;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;

import org.springframework.context.SmartLifecycle;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * QoS 1 permits duplicate delivery: consumers must be idempotent.
 *
 * @author cyrus
 */
public class MqttGateway implements SmartLifecycle {
    private final MqttProperties properties;
    private final Mqtt3AsyncClient v3;
    private final Mqtt5AsyncClient v5;
    private volatile boolean running;

    public MqttGateway(MqttProperties p) {
        properties = p;
        if (p.getQos() < 0 || p.getQos() > 2 || p.getTimeoutSeconds() < 1)
            throw new IllegalArgumentException("Invalid MQTT QoS/timeout");
        var builder =
                MqttClient.builder()
                        .identifier(p.getClientId())
                        .serverHost(p.getHost())
                        .serverPort(p.getPort())
                        .automaticReconnectWithDefaultConfig();
        if (p.isTls()) builder.sslWithDefaultConfig();
        v3 = p.isVersion5() ? null : builder.useMqttVersion3().buildAsync();
        v5 = p.isVersion5() ? builder.useMqttVersion5().buildAsync() : null;
    }

    MqttGateway(MqttProperties p, Mqtt3AsyncClient v3, Mqtt5AsyncClient v5) {
        properties = p;
        this.v3 = v3;
        this.v5 = v5;
    }

    @Override
    public synchronized void start() {
        if (running) return;
        CompletableFuture<?> connect;
        if (v3 != null) {
            var builder = v3.connectWith();
            if (properties.getUsername() != null)
                builder.simpleAuth()
                        .username(properties.getUsername())
                        .password(password())
                        .applySimpleAuth();
            connect = builder.send();
        } else {
            var builder = v5.connectWith();
            if (properties.getUsername() != null)
                builder.simpleAuth()
                        .username(properties.getUsername())
                        .password(password())
                        .applySimpleAuth();
            connect = builder.send();
        }
        try {
            connect.orTimeout(properties.getTimeoutSeconds(), TimeUnit.SECONDS).join();
            running = true;
        } catch (RuntimeException e) {
            disconnect().exceptionally(error -> null);
            throw e;
        }
    }

    private byte[] password() {
        return properties.getPassword() == null
                ? new byte[0]
                : properties.getPassword().getBytes(StandardCharsets.UTF_8);
    }

    public CompletableFuture<Void> publish(String topic, byte[] payload) {
        requireRunning();
        CompletableFuture<?> result =
                v3 != null
                        ? v3.publishWith()
                                .topic(topic)
                                .qos(MqttQos.fromCode(properties.getQos()))
                                .payload(payload)
                                .send()
                        : v5.publishWith()
                                .topic(topic)
                                .qos(MqttQos.fromCode(properties.getQos()))
                                .payload(payload)
                                .send();
        return result.thenApply(ignored -> null);
    }

    public CompletableFuture<Void> subscribe(String topic, Consumer<MqttMessage> callback) {
        requireRunning();
        CompletableFuture<?> result =
                v3 != null
                        ? v3.subscribeWith()
                                .topicFilter(topic)
                                .qos(MqttQos.fromCode(properties.getQos()))
                                .callback(
                                        msg ->
                                                callback.accept(
                                                        new MqttMessage(
                                                                msg.getTopic().toString(),
                                                                msg.getPayloadAsBytes())))
                                .send()
                        : v5.subscribeWith()
                                .topicFilter(topic)
                                .qos(MqttQos.fromCode(properties.getQos()))
                                .callback(
                                        msg ->
                                                callback.accept(
                                                        new MqttMessage(
                                                                msg.getTopic().toString(),
                                                                msg.getPayloadAsBytes())))
                                .send();
        return result.thenApply(ignored -> null);
    }

    private void requireRunning() {
        if (!running) throw new IllegalStateException("MQTT client is not connected");
    }

    private CompletableFuture<Void> disconnect() {
        return v3 != null ? v3.disconnect() : v5.disconnect();
    }

    @Override
    public synchronized void stop() {
        try {
            disconnect().orTimeout(properties.getTimeoutSeconds(), TimeUnit.SECONDS).join();
        } finally {
            running = false;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
