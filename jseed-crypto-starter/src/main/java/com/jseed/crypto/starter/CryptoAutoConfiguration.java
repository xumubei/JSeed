// SPDX-License-Identifier: Apache-2.0
package com.jseed.crypto.starter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cyrus
 */
@AutoConfiguration
@EnableConfigurationProperties(CryptoProperties.class)
@ConditionalOnProperty(
        prefix = "jseed.crypto",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class CryptoAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public CryptoService cryptoService(CryptoProperties properties) {
        return new CryptoService(properties.getPasswordStrength(), properties.getAesKey());
    }
}
