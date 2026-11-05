package com.jseed.core.starter.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cglib.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utilities for additional arguments
 *
 * @author cyrus
 * @date: 2024-07-08
 */
@Slf4j
public class FeaturesUtil {

    public static <T> T toFeatures(Map<String, Object> features, Class<T> tClass) {
        if (Objects.isNull(features)) {
            return null;
        }
        return toFeatures(JSON.toJSONString(features), tClass);
    }

    public static <T> T toFeatures(String features, Class<T> tClass) {
        if (StringUtils.isBlank(features)) {
            return null;
        }
        return JSON.parseObject(features, tClass);
    }

    public static Map<String, Object> toMap(String features) {
        return StringUtils.isBlank(features) ? Maps.newHashMap() : JSON.parseObject(features, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> Map<String, Object> toMap(Object features) {
        HashMap<String, Object> map = Maps.newHashMap();
        if (Objects.isNull(features)) {
            return map;
        }
        BeanMap beanMap = BeanMap.create(features);
        for (Object key : beanMap.keySet()) {
            Object value = beanMap.get(key);
            map.put(String.valueOf(key), value);
        }
        return map;
    }

    public static String toString(Object features) {
        if (Objects.isNull(features)) {
            return JSON.toJSONString(Maps.newHashMap());
        }
        return JSON.toJSONString(features);
    }

    public static String toString(Map<?, ?> map) {
        if (Objects.isNull(map)) {
            map = Maps.newHashMap();
        }
        return JSON.toJSONString(map);
    }


    public static Map<String, Object> merge(String features1, Object features2) {
        Map<String, Object> m1 = toMap(features1);
        Map<String, Object> m2 = toMap(features2);
        m1.putAll(m2);
        return m1;
    }

    public static Map<String, Object> merge(String features1, String features2) {
        Map<String, Object> m1 = toMap(features1);
        Map<String, Object> m2 = toMap(features2);
        m1.putAll(m2);
        return m1;
    }

    public static Map<String, Object> merge(Map<String, Object> m1, Object features2) {
        Map<String, Object> m2 = toMap(features2);
        m1.putAll(m2);
        return m1;
    }

    public static Map<String, Object> merge(Map<String, Object> m1, Map<String, Object> m2) {
        if (Objects.isNull(m1)) {
            m1 = Maps.newHashMap();
        }
        if (Objects.isNull(m2)) {
            m2 = Maps.newHashMap();
        }
        m1.putAll(m2);
        return m1;
    }

}
