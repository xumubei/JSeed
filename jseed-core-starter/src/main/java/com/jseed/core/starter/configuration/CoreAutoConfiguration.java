package com.jseed.core.starter.configuration;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.jseed.core.starter.fastjson.DateSerializer;
import com.jseed.core.starter.fastjson.LocalDateSerializer;
import com.jseed.core.starter.fastjson.LocalDateTimeSerializer;
import com.jseed.core.starter.fastjson.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Auto-configuration
 *
 * @author cyrus
 * @date: 2024-07-09
 */
@org.springframework.boot.autoconfigure.AutoConfiguration
@org.springframework.context.annotation.Import(com.jseed.core.starter.utils.SpringUtils.class)
public class CoreAutoConfiguration {
    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(com.fasterxml.jackson.databind.ObjectMapper.class)
    public com.fasterxml.jackson.databind.ObjectMapper jseedLegacyObjectMapper() {
        return com.jseed.core.starter.utils.ObjectConverter.getMapper().copy();
    }


    /**
     * Use consistent date serialization across Fastjson.
     *
     * @return
     */
    static {
        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(LocalTime.class, new LocalTimeSerializer());
        serializeConfig.put(Date.class, new DateSerializer());
        serializeConfig.put(LocalDate.class, new LocalDateSerializer());
        serializeConfig.put(LocalDateTime.class, new LocalDateTimeSerializer());
    }
}
