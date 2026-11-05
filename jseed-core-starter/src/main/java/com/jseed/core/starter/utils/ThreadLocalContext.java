package com.jseed.core.starter.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Stores the city ID and other user context obtained by AOP for propagation within the request.
 *
 * @author cyrus
 * @date: 2023-08-22
 */
public abstract class ThreadLocalContext {
    /**
     * Thread-local context
     */
    protected static final TransmittableThreadLocal<Map<String, Object>> APPLICATION_CONTEXT = new TransmittableThreadLocal<>();

    /**
     * Stores a value in the current thread's context.
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        Map<String, Object> map = APPLICATION_CONTEXT.get();
        if (CollectionUtil.isEmpty(map)) {
            map = Maps.newHashMap();
        }
        map.put(key, value);
        APPLICATION_CONTEXT.set(map);
    }

    public static void remove(String key) {
        Map<String, Object> map = APPLICATION_CONTEXT.get();
        if (CollectionUtil.isEmpty(map)) {
            return;
        }
        map.remove(key);
        APPLICATION_CONTEXT.set(map);
    }

    /**
     * Reads a value from the current thread's context.
     *
     * @return
     */
    public static Map<String, Object> getParams() {
        return APPLICATION_CONTEXT.get();
    }

    /**
     * Reads a value from the current thread's context.
     *
     * @return
     */
    public static Object getValue(String key) {
        Map<String, Object> map = APPLICATION_CONTEXT.get();
        if (CollectionUtil.isEmpty(map)) {
            return null;
        }
        return map.get(key);
    }

    /**
     * Releases the context held by the current thread.
     */
    public static void release() {
        APPLICATION_CONTEXT.remove();
    }
}
