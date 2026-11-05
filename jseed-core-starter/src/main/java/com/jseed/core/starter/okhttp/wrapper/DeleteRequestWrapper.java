package com.jseed.core.starter.okhttp.wrapper;


import com.jseed.core.starter.okhttp.model.HttpRequest;
import okhttp3.Request;

/**
 * DELETE request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class DeleteRequestWrapper implements RequestWrapper {
    private final String url;
    private final HttpRequest params;

    public DeleteRequestWrapper(String url, HttpRequest params) {
        this.url = url;
        this.params = params;
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(GetRequestWrapper.queryUrl(url, params))
                .headers(initHeaders(params))
                .delete()
                .build();
    }
}
