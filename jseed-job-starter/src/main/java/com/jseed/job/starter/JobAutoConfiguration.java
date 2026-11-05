// SPDX-License-Identifier: Apache-2.0
package com.jseed.job.starter;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cyrus
 */
@AutoConfiguration
@EnableConfigurationProperties(JobProperties.class)
@ConditionalOnProperty(prefix = "jseed.job", name = "enabled", havingValue = "true")
public class JobAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobExecutor(JobProperties p) {
        if (p.getAdminAddresses() == null
                || p.getAdminAddresses().isBlank()
                || p.getAccessToken() == null
                || p.getAccessToken().isBlank())
            throw new IllegalArgumentException(
                    "jseed.job.admin-addresses and access-token are required");
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(p.getAdminAddresses());
        executor.setAppname(p.getAppName());
        executor.setAccessToken(p.getAccessToken());
        executor.setAddress(p.getAddress());
        executor.setIp(p.getIp());
        executor.setPort(p.getPort());
        executor.setLogPath(p.getLogPath());
        executor.setLogRetentionDays(p.getLogRetentionDays());
        executor.setGlueEnabled(false);
        return executor;
    }
}
