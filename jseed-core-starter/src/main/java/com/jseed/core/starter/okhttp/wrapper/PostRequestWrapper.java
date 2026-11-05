package com.jseed.core.starter.okhttp.wrapper;


import com.jseed.core.starter.okhttp.constants.HttpContentType;
import com.jseed.core.starter.okhttp.exception.HttpClientException;
import com.jseed.core.starter.okhttp.model.HttpRequest;
import com.jseed.core.starter.okhttp.utils.FileRequestBody;
import com.jseed.core.starter.okhttp.utils.FileUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * POST request
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class PostRequestWrapper implements RequestWrapper {

    protected final String url;
    protected final HttpRequest params;

    public PostRequestWrapper(String url, HttpRequest params) {
        this.url = url;
        this.params = params;
    }

    @Override
    public Request create() {
        return new Request.Builder()
                .url(url)
                .headers(initHeaders(params))
                .post(initBody())
                .build();
    }

    protected RequestBody initBody() {
        //Handle requests without file uploads.
        if (!params.isHasFile()) {
            if (params.isJson()) {
                return RequestBody.create(params.getJsonBody(), MediaType.parse(HttpContentType.JSON));
            }
            String content = params.getParams().toString();
            return RequestBody.create(content,
                    MediaType.parse(StringUtils.isBlank(params.getContentType()) ? HttpContentType.FORM : params.getContentType())
            );
        }
        //Handle single-file uploads.
        Set<Map.Entry<String, Object>> entries = params.getParamsEntries().entrySet();
        //A single file with no additional parameters is passed as a file stream.
        if (entries.isEmpty() && null != params.getBytes()) {
            String contentType = StringUtils.isBlank(params.getContentType()) ? HttpContentType.FILE : params.getContentType();
            return FileRequestBody.create(contentType, params.getBytes(), params.isChunked());
        }
        //Handle form uploads, multiple files, or files with extra request parameters.
        if (entries.size() > 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(Objects.requireNonNull(MediaType.parse(HttpContentType.MULTIPART_FILE)));
            //Creates a file request body.
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() instanceof File) {
                    builder.addFormDataPart(entry.getKey(), entry.getKey(), RequestBody.create((File) entry.getValue(), MultipartBody.FORM));
                    continue;
                }
                if (entry.getValue() instanceof byte[]) {
                    builder.addFormDataPart(entry.getKey(), entry.getKey(),
                            RequestBody.create((byte[]) entry.getValue(), MultipartBody.FORM));
                    continue;
                }
                if (entry.getValue() instanceof InputStream) {
                    builder.addFormDataPart(entry.getKey(), entry.getKey(),
                            RequestBody.create(FileUtil.streamToByteArray((InputStream) entry.getValue()), MultipartBody.FORM));
                    continue;
                }
                builder.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }
            return builder.build();
        }
        throw new HttpClientException("error request body!");
    }


}