// SPDX-License-Identifier: Apache-2.0
package com.jseed.netty.starter;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Data
@ConfigurationProperties("jseed.netty")
public class NettyProperties {
    private boolean enabled;
    private boolean serverEnabled;
    private String host = "127.0.0.1";
    private int port = 8091;
    private int workerThreads = 2;
    private int connectTimeoutMillis = 10000;
}
