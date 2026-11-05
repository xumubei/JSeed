package com.jseed.core.starter.fastjson;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Fastjson date and time serialization
 *
 * @author cyrus
 * @date: 2024-07-09
 */
public class DateSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
        } else {
            Date result = (Date) object;
            out.writeString(DateUtil.format(result, DatePattern.NORM_DATETIME_PATTERN));
        }
    }

}