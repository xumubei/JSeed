package com.jseed.web.starter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import tools.jackson.core.StreamWriteFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author cyrus
 */
public class JacksonSerializer {
    @Bean
    public JsonMapperBuilderCustomizer jseedJsonCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("jseed-web");
            BigNumberSerializer serializer = new BigNumberSerializer(Number.class);
            module.addSerializer(Long.class, serializer);
            module.addSerializer(Long.TYPE, serializer);
            module.addSerializer(BigInteger.class, serializer);
            builder.addModule(module)
                    .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                    .withConfigOverride(
                            LocalDateTime.class,
                            config ->
                                    config.setFormat(
                                            JsonFormat.Value.forPattern("yyyy-MM-dd HH:mm:ss")))
                    .withConfigOverride(
                            LocalDate.class,
                            config -> config.setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd")))
                    .withConfigOverride(
                            LocalTime.class,
                            config -> config.setFormat(JsonFormat.Value.forPattern("HH:mm:ss")))
                    .changeDefaultPropertyInclusion(
                            value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
                    .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .enable(StreamWriteFeature.WRITE_BIGDECIMAL_AS_PLAIN);
        };
    }
}
