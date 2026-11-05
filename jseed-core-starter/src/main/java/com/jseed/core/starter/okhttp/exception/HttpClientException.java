package com.jseed.core.starter.okhttp.exception;

import java.lang.reflect.Method;

/**
 * HTTP client exception
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String format, Method method) {
        throw new HttpClientException(String.format("%s \n -> Error by: %s#%s", format, method.getDeclaringClass().getName(), method.getName()));
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
