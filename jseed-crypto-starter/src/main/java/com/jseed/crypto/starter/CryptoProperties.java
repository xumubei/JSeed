// SPDX-License-Identifier: Apache-2.0
package com.jseed.crypto.starter;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Getter
@Setter
@ConfigurationProperties("jseed.crypto")
public class CryptoProperties {
    private boolean enabled = true;
    private int passwordStrength = 12;
    private String aesKey;
}
