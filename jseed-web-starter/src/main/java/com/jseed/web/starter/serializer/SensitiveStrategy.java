package com.jseed.web.starter.serializer;

import cn.hutool.core.util.DesensitizedUtil;
import lombok.AllArgsConstructor;

import java.util.function.Function;

/**
 * Masking strategies
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@AllArgsConstructor
public enum SensitiveStrategy {
    /**
     * Mask a national ID number.
     */
    ID_CARD(s -> DesensitizedUtil.idCardNum(s, 3, 4)),

    /**
     * Mask a mobile phone number.
     */
    PHONE(DesensitizedUtil::mobilePhone),

    /**
     * Mask an address.
     */
    ADDRESS(s -> DesensitizedUtil.address(s, 8)),

    /**
     * Mask an email address.
     */
    EMAIL(DesensitizedUtil::email),

    /**
     * Bank card number
     */
    BANK_CARD(DesensitizedUtil::bankCard),

    /**
     * Chinese name
     */
    CHINESE_NAME(DesensitizedUtil::chineseName),

    /**
     * ipV4
     */
    IPV4(DesensitizedUtil::ipv4),
    ;

    /**
     * Masking function
     */
    private final Function<String, String> desensitizer;

    public Function<String, String> desensitizer() {
        return desensitizer;
    }
}
