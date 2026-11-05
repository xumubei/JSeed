package com.jseed.core.starter.okhttp.interceptor;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Logs timing and status without consuming streams or exposing credentials.
 *
 * @author cyrus
 */
public class LogInterceptor implements Interceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long started = System.nanoTime();
        Response response = chain.proceed(request);
        LOG.info(
                "HTTP {} host={} status={} durationMs={}",
                request.method(),
                request.url().host(),
                response.code(),
                (System.nanoTime() - started) / 1_000_000);
        return response;
    }
}
