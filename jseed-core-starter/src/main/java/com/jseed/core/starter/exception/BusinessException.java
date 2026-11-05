package com.jseed.core.starter.exception;

import com.jseed.core.starter.enums.ResponseStatusEnum;
import lombok.Getter;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * Custom business exception
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    private final String msg;

    /**
     * Custom response message
     *
     * @param code    response status
     * @param message detailed response message
     * @param params  values to fill message placeholders
     */
    public BusinessException(Integer code, String message, Throwable e, Object... params) {
        super(message, e);
        this.code = code;
        this.msg = getMessage(message, params);
    }

    /**
     * Standard response
     *
     * @param status response status
     */
    public BusinessException(ResponseStatusEnum status, Object... params) {
        super(status.getMsg());
        this.code = status.getCode();
        this.msg = getMessage(status.getMsg(), params);
    }

    /**
     * Custom response message
     *
     * @param status  response status
     * @param message detailed response message
     * @param message detailed response message
     */
    public BusinessException(ResponseStatusEnum status, String message, Object... params) {
        super(message);
        this.code = status.getCode();
        this.msg = getMessage(message, params);
    }

    /**
     * Custom response message
     *
     * @param code    response status
     * @param message detailed response message
     * @param params  values to fill message placeholders
     */
    public BusinessException(Integer code, String message, Object... params) {
        super(message);
        this.code = code;
        this.msg = getMessage(message, params);
    }

    /**
     * Formats the message using SLF4J conventions.
     *
     * @param msg  message template
     * @param args template arguments
     * @return
     */
    private String getMessage(String msg, Object... args) {
        FormattingTuple formattingTuple = MessageFormatter.arrayFormat(msg, args);
        return formattingTuple.getMessage();
    }
}
