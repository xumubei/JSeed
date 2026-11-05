package com.jseed.core.starter.okhttp.wrapper;


import com.jseed.core.starter.okhttp.model.HttpRequest;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * GET request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class GetRequestWrapper implements RequestWrapper {
    private final String url;
    private final HttpRequest params;

    public GetRequestWrapper(String url, HttpRequest params) {
        this.url = url;
        this.params = params;
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(queryUrl(url, params))
                .headers(initHeaders(params))
                .get()
                .build();
    }

    static HttpUrl queryUrl(String url, HttpRequest params) {
        if (url.regionMatches(true, 0, "ws:", 0, 3)) {
            url = "http:" + url.substring(3);
        } else if (url.regionMatches(true, 0, "wss:", 0, 4)) {
            url = "https:" + url.substring(4);
        }

        HttpUrl.Builder builder = HttpUrl.get(url).newBuilder();
        for (Map.Entry<String, Object> entry : params.getParamsEntries().entrySet()) {
            Object value = entry.getValue();
            if (!(value instanceof File) && !(value instanceof byte[])) {
                builder.addQueryParameter(String.valueOf(entry.getKey()), Objects.toString(value, ""));
            }
        }
        return builder.build();
    }
}
