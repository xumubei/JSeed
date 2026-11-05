package com.jseed.core.starter.okhttp.wrapper;


import com.jseed.core.starter.okhttp.model.HttpRequest;

/**
 * WebSocket request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class WsRequestWrapper extends GetRequestWrapper {
    public WsRequestWrapper(String url, HttpRequest params) {
        super(url, params);
    }
}
