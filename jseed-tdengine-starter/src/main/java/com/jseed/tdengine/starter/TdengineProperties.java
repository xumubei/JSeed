// SPDX-License-Identifier: Apache-2.0
package com.jseed.tdengine.starter;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Getter
@Setter
@ConfigurationProperties("jseed.tdengine")
public class TdengineProperties {
    private boolean enabled;
    private String url = "jdbc:TAOS-WS://localhost:6041/jseed";
    private String username;
    private String password;
    private int maximumPoolSize = 5;
}
