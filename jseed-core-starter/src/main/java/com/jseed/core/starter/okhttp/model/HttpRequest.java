package com.jseed.core.starter.okhttp.model;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP request parameters
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class HttpRequest {
    /**
     * Parameter storage
     */
    private final Map<String, Object> paramsEntries = new HashMap<>(8);

    /**
     * Request header storage
     */
    private final HttpHeaders mHttpHeaders = HttpHeaders.builder();

    /**
     * File stream
     */
    private byte[] bytes = null;

    /**
     * Whether the request uploads files
     */
    @Setter
    @Getter
    private boolean hasFile;

    /**
     * Request content type
     */
    @Getter
    @Setter
    private String contentType = null;

    /**
     * Whether the request contains JSON
     */
    private boolean isJson;

    /**
     * JSON request body
     */
    private Object bodyObj = null;

    /**
     * Only applies when hasFile is true.
     * Whether to use chunks. Applies only to file uploads.
     */
    private Boolean chunked = Boolean.FALSE;

    public byte[] getBytes() {
        return null == this.bytes ? getParams().toString().getBytes() : this.bytes;
    }

    public void putHeaders(HttpHeaders header) {
        mHttpHeaders.add(header);
    }

    public void putHeaders(final String key, final String value) {
        mHttpHeaders.add(key, value);
    }

    /**
     * Adds a parameter.
     */
    public void put(String key, final Object value) {
        paramsEntries.put(key, value);
    }

    /**
     * Adds a file parameter for upload.
     */
    public void put(final String key, final File file) {
        hasFile = true;
        paramsEntries.put(key, file);
    }

    public void put(final String key, final byte[] file) {
        hasFile = true;
        paramsEntries.put(key, file);
    }

    public void put(final String key, final InputStream file) {
        hasFile = true;
        paramsEntries.put(key, file);
    }

    public void put(byte[] bytes) {
        try {
            hasFile = true;
            this.bytes = bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StringBuilder getParams() {
        StringBuilder result = new StringBuilder();
        boolean isFirst = true;

        for (Map.Entry<String, Object> entry : paramsEntries.entrySet()) {
            if (entry.getValue() instanceof File) {
                continue;
            }
            if (entry.getValue() instanceof byte[]) {
                continue;
            }
            if (!isFirst) {
                result.append("&");
            } else {
                isFirst = false;
            }
            try {
                Object value = entry.getValue();
                if (value == null) {
                    value = "";
                }
                result.append(entry.getKey()).append("=").append(URLEncoder.encode(value.toString(), "utf8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public Map<String, Object> getParamsEntries() {
        return paramsEntries;
    }

    public HttpHeaders getHeaders() {
        return mHttpHeaders;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }

    /**
     * Returns the request content as a JSON object.
     *
     * @return String
     */
    public String getJsonBody() {
        if (isJson) {
            return null == bodyObj ? JSON.toJSONString(paramsEntries) : JSON.toJSONString(bodyObj);
        } else {
            return null;
        }
    }

    public void setJsonBody(Object obj) {
        this.bodyObj = obj;
    }

    public void setChunked() {
        this.chunked = Boolean.TRUE;
    }

    public boolean isChunked() {
        return chunked;
    }
}
