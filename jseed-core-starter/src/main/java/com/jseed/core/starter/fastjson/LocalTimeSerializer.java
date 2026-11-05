package com.jseed.core.starter.fastjson;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Fastjson date and time serialization
 *
 * @author cyrus
 * @date: 2024-07-09
 */
public class LocalTimeSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            LocalTime result = (LocalTime) object;
            out.writeString(result.format(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
        }
    }

}