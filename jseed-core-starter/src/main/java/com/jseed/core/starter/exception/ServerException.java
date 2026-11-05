package com.jseed.core.starter.exception;

import lombok.Getter;

/**
 * Custom service exception
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@Getter
public class ServerException extends RuntimeException {

    /**
     * Custom response message
     *
     * @param message detailed response message
     * @param message detailed response message
     */
    public ServerException(String message) {
        super(message);
    }
}
