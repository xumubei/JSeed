package com.jseed.web.starter;


import com.jseed.core.starter.utils.ThreadLocalContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Response body advice
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@RestControllerAdvice
public class ResponseReturnHandler implements ResponseBodyAdvice<Object> {

    /**
     * Determines whether beforeBodyWrite should run. Defaults to true.
     * To restrict this advice to annotated methods, check for a custom annotation in supports.
     */
    @Override
    public boolean supports(@Nullable MethodParameter returnType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * Processes the response body.
     */
    @Override
    public Object beforeBodyWrite(Object body, @Nullable MethodParameter returnType, @Nullable MediaType selectedContentType, @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nullable ServerHttpRequest request, @Nullable ServerHttpResponse response) {
        // Release the context for the request thread.
        ThreadLocalContext.release();
        // Return the body as is to avoid wrapping it twice.
        return body;
    }
}
