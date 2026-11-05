package com.jseed.core.starter.utils;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Object conversion utilities
 *
 * @author cyrus
 * @date: 2024-07-09
 */
public class ObjectConverter {
    /**
     * Use Jackson to convert between object types.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Register the date and time serialization module.
     */
    static {
        // Support Date serialization and deserialization.
        MAPPER.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN));
        // Java 8 date and time support
        MAPPER.registerModule(getJavaTimeModule());
        // Omit empty properties such as null values and empty strings.
        // Do not fail when serializing empty beans.
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // Ignore unknown properties during deserialization.
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // Write BigDecimal values without scientific notation.
        MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
    }


    /**
     * Converts an object to another type.
     *
     * @param source source object
     * @param tClass target type
     * @param <T>
     * @return
     */
    public static <T> T convertObj(Object source, Class<T> tClass) {
        return Objects.isNull(source) ? null : MAPPER.convertValue(source, tClass);
    }

    /**
     * Converts the items of a list to another type.
     *
     * @param sourceList source list
     * @param targetClass target type
     * @param <S> source type
     * @param <T> target type
     * @return converted list
     */
    public static <S, T> List<T> convertList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> convertObj(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * Serializes the object to JSON. Throws JsonProcessingException if serialization fails.
     *
     * @param source
     * @return
     */
    @SneakyThrows
    public static String toJsonString(Object source) {
        return MAPPER.writeValueAsString(source);
    }

    /**
     * Jackson module for date and time formats
     *
     * @return
     */
    public static JavaTimeModule getJavaTimeModule() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        return javaTimeModule;
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
