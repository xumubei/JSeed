// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.mqtt.starter.MqttGateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author cyrus
 */
@Profile("mqtt")
@RestController
@RequestMapping("/demo/mqtt")
public class MqttDemo {
    private static final Logger LOG = LoggerFactory.getLogger(MqttDemo.class);
    private static final String TOPIC = "jseed/demo";
    private final MqttGateway mqtt;

    public MqttDemo(MqttGateway mqtt) {
        this.mqtt = mqtt;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() throws Exception {
        mqtt.subscribe(
                        TOPIC,
                        message ->
                                LOG.info(
                                        "MQTT topic={}, bytes={}",
                                        message.topic(),
                                        message.payload().length))
                .get(10, TimeUnit.SECONDS);
    }

    @PostMapping
    public void publish(@RequestBody String value) throws Exception {
        mqtt.publish(TOPIC, value.getBytes(StandardCharsets.UTF_8)).get(10, TimeUnit.SECONDS);
    }
}
