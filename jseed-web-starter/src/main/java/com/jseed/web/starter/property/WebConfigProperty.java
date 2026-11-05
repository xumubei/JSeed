package com.jseed.web.starter.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Web request settings
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Data
@ConfigurationProperties(prefix = "web-config")
public class WebConfigProperty {

    /**
     * Whether to mask sensitive data. Enabled by default.
     */
    private boolean desensitization = true;

    /**
     * Allowed origins for cross-origin requests, as hostnames or IP addresses.
     */
    private List<String> allowedOrigins;

    /**
     * XSS settings
     */
    private XssProperty xss = new XssProperty();

    /**
     * API encryption settings
     */
    private EncryptionProperty apiEncrypt = new EncryptionProperty();


    /**
     * XSS filter settings
     *
     * @author cyrus
     */
    @Data
    public static class XssProperty {

        /**
         * Whether the filter is enabled
         */
        private String enabled;

        /**
         * Excluded URLs, separated by commas
         */
        private String excludes;

        /**
         * URL patterns to match
         */
        private String urlPatterns;

    }

    @Data
    public static class EncryptionProperty {

        /**
         * Whether API encryption is enabled
         */
        private boolean enabled;

        /**
         * AES key
         */
        @lombok.ToString.Exclude
        private String aesKey;

    }

}
