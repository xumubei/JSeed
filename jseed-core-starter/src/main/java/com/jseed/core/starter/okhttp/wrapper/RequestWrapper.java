package com.jseed.core.starter.okhttp.wrapper;

import com.jseed.core.starter.okhttp.model.HttpHeaders;
import com.jseed.core.starter.okhttp.model.HttpRequest;
import okhttp3.Headers;
import okhttp3.Request;


/**
 * @author cyrus
 */
public interface RequestWrapper {

    /**
     * Default request header builder
     *
     * @param params
     * @return
     */
    default Headers initHeaders(HttpRequest params) {
        // Creates headers.
        HttpHeaders httpHeaders = params.getHeaders();
        Headers.Builder headerBuilder = new Headers.Builder();
        httpHeaders.foreach(headerBuilder::add);
        return headerBuilder.build();
    }

    /**
     * Builds the request.
     *
     * @return
     */
    Request create();
}