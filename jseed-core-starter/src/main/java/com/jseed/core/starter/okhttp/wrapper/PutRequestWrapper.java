package com.jseed.core.starter.okhttp.wrapper;

import com.jseed.core.starter.okhttp.model.HttpRequest;
import okhttp3.Request;

/**
 * PUT request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class PutRequestWrapper extends PostRequestWrapper {
    public PutRequestWrapper(String url, HttpRequest params) {
        super(url, params);
    }


    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(super.params))
                .put(super.initBody())
                .build();
    }
}
