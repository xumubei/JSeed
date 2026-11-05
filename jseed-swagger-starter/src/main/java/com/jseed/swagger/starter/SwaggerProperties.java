// SPDX-License-Identifier: Apache-2.0
package com.jseed.swagger.starter;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Data
@ConfigurationProperties("jseed.swagger")
public class SwaggerProperties {
    private boolean enabled = true;
    private String title = "JSeed API";
    private String description = "";
    private String version = "1.0";
}
