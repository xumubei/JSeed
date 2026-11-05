package com.jseed.core.starter.fastjson;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Fastjson date and time serialization
 *
 * @author cyrus
 * @date: 2024-07-09
 */
public class LocalDateTimeSerializer implements ObjectSerializer {

    public LocalDateTimeSerializer() {
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            LocalDateTime result = (LocalDateTime) object;
            out.writeString(result.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN)));
        }
    }

}