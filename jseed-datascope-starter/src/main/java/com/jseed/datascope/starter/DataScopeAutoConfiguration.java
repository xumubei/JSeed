// SPDX-License-Identifier: Apache-2.0
package com.jseed.datascope.starter;

import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.jseed.mybatis.starter.property.SplitTableProperty;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.context.annotation.Bean;

import java.util.Set;

/**
 * @author cyrus
 */
@AutoConfiguration(before = com.jseed.mybatis.starter.MybatisConfiguration.class)
@ConditionalOnProperty(prefix = "jseed.datascope", name = "enabled", havingValue = "true")
public class DataScopeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public DataScopeResolver dataScopeResolver() {
        return scope -> Set.of();
    }

    @Bean
    @ConditionalOnMissingBean(DataPermissionInterceptor.class)
    public DataPermissionInterceptor dataPermissionInterceptor(
            DataScopeResolver resolver, SplitTableProperty split) {
        return new ScopePermissionInterceptor(new ScopePermissionHandler(resolver, split));
    }
}
