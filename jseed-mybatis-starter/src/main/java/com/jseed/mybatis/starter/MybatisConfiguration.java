package com.jseed.mybatis.starter;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.jseed.mybatis.starter.interceptor.MybatisTableNameChangeInterceptor;
import com.jseed.mybatis.starter.property.MybatisPlusProperty;
import com.jseed.mybatis.starter.property.SplitTableProperty;
import com.jseed.mybatis.starter.util.TransactionUtil;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * @author cyrus
 */
@AutoConfiguration(
        beforeName = "com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration",
        afterName =
                "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration")
@ConditionalOnProperty(
        prefix = "jseed.mybatis",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
@EnableConfigurationProperties({MybatisPlusProperty.class, SplitTableProperty.class})
public class MybatisConfiguration {
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "mybatis-plus",
            name = "custom-interceptor",
            havingValue = "false",
            matchIfMissing = true)
    public MybatisPlusInterceptor mybatisPlusInterceptor(
            SplitTableProperty split, ObjectProvider<InnerInterceptor> extensions) {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (split.getTables() != null && !split.getTables().isEmpty()) {
            interceptor.addInnerInterceptor(new MybatisTableNameChangeInterceptor(split));
        }
        extensions.orderedStream().forEach(interceptor::addInnerInterceptor);
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        PaginationInnerInterceptor pages = new PaginationInnerInterceptor();
        pages.setMaxLimit(500L);
        interceptor.addInnerInterceptor(pages);
        return interceptor;
    }

    @Bean
    @ConditionalOnBean(DataSourceTransactionManager.class)
    @ConditionalOnMissingBean
    public TransactionUtil transactionUtil(DataSourceTransactionManager manager) {
        return new TransactionUtil(manager);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdentifierGenerator idGenerator() {
        return new DefaultIdentifierGenerator();
    }
}
