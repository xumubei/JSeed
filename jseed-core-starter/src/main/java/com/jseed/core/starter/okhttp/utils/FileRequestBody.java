package com.jseed.core.starter.okhttp.utils;

import com.jseed.core.starter.okhttp.constants.HttpContentType;
import com.jseed.core.starter.okhttp.exception.HttpClientException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Request body
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class FileRequestBody {

    public static RequestBody create(String contentType, final byte[] bytes, boolean isChunked) {
        if (bytes == null) {
            throw new HttpClientException("file content must be not null!");
        }
        return isChunked
                ? createChunkedFileRequestBody(contentType, bytes)
                : createFileRequestBody(contentType, bytes);
    }

    /**
     * Creates a request from a file stream.
     */
    private static RequestBody createFileRequestBody(String contentType, final byte[] bytes) {
        return new RequestBody() {
            @Override
            @Nullable
            public MediaType contentType() {
                return MediaType.parse(null == contentType ? HttpContentType.FILE : contentType);
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) throws IOException {
                sink.write(bytes, 0, bytes.length);
            }
        };
    }

    /**
     * Creates a chunked file-stream request.
     */
    public static RequestBody createChunkedFileRequestBody(String contentType, final byte[] bytes) {
        return new RequestBody() {
            //Calculates the number of chunks.
            int offset = 0;
            int count = 0;
            final int segmentSize = 4096;

            @Override
            @Nullable
            public MediaType contentType() {
                return MediaType.parse(contentType);
            }

            @Override
            public long contentLength() {
                return bytes.length;
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) {
                count = bytes.length / segmentSize + (bytes.length % segmentSize != 0 ? 1 : 0);
                for (int i = 0; i < count; i++) {
                    int chunk = (i < count - 1) ? segmentSize : bytes.length - offset;
                    sink.getBuffer().write(bytes, offset, chunk);//Write SEGMENT_SIZE bytes at a time.
                    sink.getBuffer().flush();
                    offset += chunk;
                }
            }
        };
    }
}
