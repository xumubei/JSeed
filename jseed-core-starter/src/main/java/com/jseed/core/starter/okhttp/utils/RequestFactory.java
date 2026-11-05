package com.jseed.core.starter.okhttp.utils;


import com.jseed.core.starter.okhttp.exception.HttpClientException;
import com.jseed.core.starter.okhttp.model.HttpMethod;
import com.jseed.core.starter.okhttp.model.HttpRequest;
import com.jseed.core.starter.okhttp.wrapper.*;
import okhttp3.Request;

/**
 * Request factory
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class RequestFactory {

    private final HttpMethod method;
    private final HttpRequest params;
    private final String url;

    /**
     * Uses the factory to build the corresponding OkHttp request.
     *
     * @param method HTTP method
     * @param params HTTP request parameters
     * @param url    HTTP request URL
     */
    public RequestFactory(HttpMethod method, HttpRequest params, String url) {
        this.method = method;
        this.params = params;
        this.url = url;
    }

    /**
     * Builds the request body according to its content type.
     *
     * @return
     */
    public Request initRequest() {
        switch (method) {
            case PATCH:
                return new PatchRequestWrapper(url, params).create();
            case DELETE:
                return new DeleteRequestWrapper(url, params).create();
            case GET:
                return new GetRequestWrapper(url, params).create();
            case WS:
                return new WsRequestWrapper(url, params).create();
            case PUT:
                return new PutRequestWrapper(url, params).create();
            case POST:
                return new PostRequestWrapper(url, params).create();
            default: {
                throw new HttpClientException(String.format("不支持的请求方式: [%s]", method));
            }
        }
    }

}

