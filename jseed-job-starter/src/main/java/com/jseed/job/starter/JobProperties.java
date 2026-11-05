// SPDX-License-Identifier: Apache-2.0
package com.jseed.job.starter;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cyrus
 */
@Getter
@Setter
@ConfigurationProperties("jseed.job")
public class JobProperties {
    private boolean enabled;
    private String adminAddresses;
    private String appName = "jseed-executor";
    private String accessToken;
    private String address;
    private String ip;
    private int port = 9999;
    private String logPath = "./logs/xxl-job";
    private int logRetentionDays = 30;
}
