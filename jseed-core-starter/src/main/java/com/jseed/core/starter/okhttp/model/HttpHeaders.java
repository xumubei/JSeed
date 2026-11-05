package com.jseed.core.starter.okhttp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * Request header builder
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class HttpHeaders {

    /**
     * Request header storage
     */
    private final Map<String, String> headers = new HashMap<>();

    /**
     * Private constructor
     */
    private HttpHeaders() {
    }

    /**
     * Constructor
     */
    public static HttpHeaders builder() {
        return new HttpHeaders();
    }

    /**
     * Adds a request header.
     *
     * @param key   header name
     * @param value header value
     */
    public HttpHeaders add(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpHeaders add(HttpHeaders httpHeaders) {
        if (null != httpHeaders) {
            this.headers.putAll(httpHeaders.toMap());
        }
        return this;
    }

    /**
     * Iterator
     *
     * @param consumer
     */
    public void foreach(BiConsumer<String, String> consumer) {
        headers.forEach(consumer);
    }

    /**
     * Returns all request headers.
     *
     * @return
     */
    public Map<String, String> toMap() {
        return this.headers;
    }
}
