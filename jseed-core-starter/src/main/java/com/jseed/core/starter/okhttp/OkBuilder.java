package com.jseed.core.starter.okhttp;


import com.jseed.core.starter.okhttp.exception.HttpClientException;
import com.jseed.core.starter.okhttp.interceptor.GzipRequestInterceptor;
import com.jseed.core.starter.okhttp.interceptor.LogInterceptor;
import com.jseed.core.starter.okhttp.model.HttpHeaders;
import com.jseed.core.starter.okhttp.model.HttpMethod;
import com.jseed.core.starter.okhttp.model.HttpRequest;
import com.jseed.core.starter.okhttp.utils.FileUtil;
import com.jseed.core.starter.okhttp.utils.RequestFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Builder for OkHttp requests
 *
 * @author cyrus
 * @date: 2024-07-08
 */
@Slf4j
public final class OkBuilder {
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    /**
     * Request URL
     */
    private final String url;

    /**
     * Whether to log requests
     */
    private boolean printLog;

    /**
     * Request parameters
     */
    private final HttpRequest request = new HttpRequest();

    /**
     * Whether to enable gzip
     */
    private boolean isGzip = false;

    /**
     * HTTP method
     */
    private HttpMethod method = HttpMethod.GET;

    private OkBuilder(String url) {
        this.url = url;
    }

    /**
     * Creates a builder instance.
     *
     * @param url
     * @return
     */
    public static OkBuilder url(String url) {
        return new OkBuilder(url);
    }

    /**
     * Sets the HTTP method.
     *
     * @param method
     * @return
     */
    public OkBuilder method(HttpMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Whether to enable gzip
     *
     * @param isGzip
     * @return
     */
    public OkBuilder gzip(boolean isGzip) {
        this.isGzip = isGzip;
        return this;
    }

    /**
     * Disables request logging.
     *
     * @return
     */
    public OkBuilder disableLog() {
        this.printLog = false;
        return this;
    }

    public OkBuilder enableLog() {
        this.printLog = true;
        return this;
    }

    /**
     * Sets request headers.
     *
     * @param key   key
     * @param value value
     * @return
     */
    public OkBuilder header(String key, String value) {
        request.putHeaders(key, value);
        return this;
    }

    /**
     * Adds request headers.
     *
     * @param headers
     * @return
     */
    public OkBuilder headers(HttpHeaders headers) {
        request.putHeaders(headers);
        return this;
    }

    /**
     * Sets the content type.
     *
     * @param contentType
     * @return
     */
    public OkBuilder contentType(String contentType) {
        request.setContentType(contentType);
        return this;
    }

    public OkBuilder params(Map<String, Object> map) {
        request(map);
        return this;
    }

    public OkBuilder params(String key, String value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, Collection<? extends Serializable> value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, int value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, long value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, boolean value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, float value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, double value) {
        request.put(key, value + "");
        return this;
    }

    public OkBuilder params(String key, char value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, File value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, byte[] value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder params(String key, InputStream value) {
        request.put(key, value);
        return this;
    }

    public OkBuilder hasFile(boolean hasFile) {
        request.setHasFile(hasFile);
        return this;
    }


    public OkBuilder body(File file) {
        return body(FileUtil.fileToByteArray(file));
    }

    public OkBuilder body(InputStream stream) {
        return body(FileUtil.streamToByteArray(stream));
    }

    public OkBuilder body(byte[] file) {
        request.put(file);
        return this;
    }

    public OkBuilder transferEncoding() {
        if (HttpMethod.GET.equals(method)) {
            throw new HttpClientException("http method can not be get!");
        }
        request.setChunked();
        request.putHeaders("Transfer-Encoding", "chunked");
        return this;
    }

    /**
     * Populates parameters from a map.
     *
     * @param map
     * @return
     */
    public OkBuilder request(Map<String, Object> map) {
        if (!map.isEmpty()) {
            for (Map.Entry<String, Object> stringObjectEntry : map.entrySet()) {
                request.put(stringObjectEntry.getKey(), stringObjectEntry.getValue());
            }
        }
        return this;
    }

    /**
     * Sets a JSON request body.
     *
     * @return
     */
    public OkBuilder isJson(Object obj) {
        request.setJson(Boolean.TRUE);
        request.setJsonBody(obj);
        return this;
    }

    /**
     * Enables a JSON POST request.
     *
     * @return
     */
    public OkBuilder isJson() {
        return this.isJson(null);
    }

    /**
     * Executes synchronously and returns the response content.
     *
     * @return
     */
    public String execute() {
        try (Response response = beforeExecute()) {
            if (null != response) {
                return response.body().string();
            }
        } catch (IOException e) {
            log.error("execute error:", e);
        }
        return null;
    }

    /**
     * Builds a WebSocket request.
     *
     * @return
     */
    public WebSocket initWs(WebSocketListener listener) {
        if (StringUtils.isBlank(url) || !(url.contains("ws://") || url.contains("wss://"))) {
            throw new HttpClientException("Url can not be supported !");
        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                //Set the read timeout.
                .readTimeout(3, TimeUnit.SECONDS)
                //Set the write timeout.
                .writeTimeout(3, TimeUnit.SECONDS)
                //Set the connection timeout.
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        //Build the request.
        return mOkHttpClient.newWebSocket(initRequest(), listener);
    }


    /**
     * Builds a WebSocket request with ping/pong keepalive.
     *
     * @return
     */
    public WebSocket initPingWs(WebSocketListener listener) {
        if (StringUtils.isBlank(url) || !(url.contains("ws://") || url.contains("wss://"))) {
            throw new HttpClientException("Url can not be supported !");
        }
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                //Set the read timeout.
                .readTimeout(3, TimeUnit.SECONDS)
                //Set the write timeout.
                .writeTimeout(3, TimeUnit.SECONDS)
                //Set the connection timeout.
                .connectTimeout(3, TimeUnit.SECONDS)
                .pingInterval(40, TimeUnit.SECONDS)
                .build();

        //Build the request.
        return mOkHttpClient.newWebSocket(initRequest(), listener);
    }

    /**
     * Executes synchronously and returns the response content.
     *
     * @return
     */
    public InputStream executeStream() {
        try (Response response = beforeExecute()) {
            if (null != response) {
                return response.body().byteStream();
            }
        }
        return null;
    }

    /**
     * Executes synchronously and returns the response content.
     *
     * @return
     */
    public byte[] executeBytes() {
        try (Response response = beforeExecute()) {
            if (null != response) {
                return response.body().bytes();
            }
        } catch (IOException e) {
            log.error("execute error:", e);
        }
        return null;
    }

    /**
     * Executes synchronously and returns the response content.
     *
     * @return
     */
    private Response response() {
        try (Response response = beforeExecute()) {
            return response;
        }
    }


    private Response beforeExecute() {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("Url can not be null!");
        }
        //Build the request.
        Request request = initRequest();
        try {
            OkHttpClient.Builder builder = HTTP_CLIENT.newBuilder();
            if (isGzip) {
                builder.addInterceptor(new GzipRequestInterceptor());
            }
            if (printLog) {
                builder.addInterceptor(new LogInterceptor());
            }
            return builder.build().newCall(request).execute();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Builds the request using a factory that selects the appropriate HTTP method.
     *
     * @return
     */
    private Request initRequest() {
        return new RequestFactory(method, request, url).initRequest();
    }
}
