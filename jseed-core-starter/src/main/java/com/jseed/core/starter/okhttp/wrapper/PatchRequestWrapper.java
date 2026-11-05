package com.jseed.core.starter.okhttp.wrapper;

import com.jseed.core.starter.okhttp.model.HttpRequest;
import okhttp3.Request;

/**
 * PATCH request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class PatchRequestWrapper extends PostRequestWrapper {

    public PatchRequestWrapper(String url, HttpRequest params) {
        super(url, params);
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(super.params))
                .patch(super.initBody())
                .build();
    }
}
