package com.jseed.web.starter;

import com.jseed.core.starter.enums.ResponseStatusEnum;
import com.jseed.core.starter.model.R;
import com.jseed.core.starter.utils.ThreadLocalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

/**
 * Global exception handling
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Slf4j
@RestControllerAdvice(annotations = {Controller.class, RestController.class})
public class GlobalExceptionHandler {

    /**
     * Returns details for a request body validation failure.
     *
     * @param exception validation exception
     * @return description of the validation failure
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R<Void> handleBodyValidException(HttpServletRequest req, MethodArgumentNotValidException exception) {
        String message = exception.getMessage();
        String paramEntity = message.substring(message.indexOf("(") + 1, message.lastIndexOf(")"));
        BindingResult br = exception.getBindingResult();
        String errorMessage = (br.getFieldError() != null) ? MessageFormat.format("fieldName:[{0}] filedValue:[{1}] error:[{2}]", br.getFieldError().getField(), br.getFieldError().getRejectedValue(), br.getFieldError().getDefaultMessage()) : "";
        String outErrorMessage = (br.getFieldError() != null) ? br.getFieldError().getDefaultMessage() : "";
        log.warn("URI:{} \n Entity:{} \n Message:{}", req.getRequestURI(), paramEntity, errorMessage);
        ThreadLocalContext.release();
        return R.fail(ResponseStatusEnum.BAD_REQUEST, outErrorMessage);
    }

    /**
     * Handles a missing or unreadable request body.
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public R<Void> httpMessageNotReadableExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException e) {
        log.error("URI:{}", req.getRequestURI(), e);
        ThreadLocalContext.release();
        return R.fail(ResponseStatusEnum.BAD_REQUEST, " request body empty or error ");
    }

    /**
     * Returns an error for an unsupported HTTP method.
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public R<Void> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        ThreadLocalContext.release();
        return R.fail(ResponseStatusEnum.METHOD_NOT_ALLOWED, " http method is not allowed ");
    }

}
