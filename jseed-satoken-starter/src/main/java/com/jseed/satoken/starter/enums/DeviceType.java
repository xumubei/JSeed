package com.jseed.satoken.starter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Device type
 *
 * @author cyrus
 */
@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * Desktop client
     */
    PC("pc"),
    /**
     * Mobile app
     */
    APP("app"),
    /**
     * WeChat Mini Program
     */
    WX("wx"),
    ;

    private final String device;
}
