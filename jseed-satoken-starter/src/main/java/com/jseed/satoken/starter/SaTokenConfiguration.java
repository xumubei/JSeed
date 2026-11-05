package com.jseed.satoken.starter;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;

import com.jseed.redis.starter.RedisConfiguration;
import com.jseed.redis.starter.util.RedisUtil;
import com.jseed.satoken.starter.config.PlusSaTokenDao;
import com.jseed.satoken.starter.config.SaTokenExceptionHandler;
import com.jseed.satoken.starter.config.SaTokenWebMvcConfigurer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cyrus
 */
@AutoConfiguration(after = RedisConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jseed.satoken", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SecurityProperty.class)
public class SaTokenConfiguration {
    @Bean
    @ConditionalOnBean(RedisUtil.class)
    @ConditionalOnMissingBean(SaTokenDao.class)
    public PlusSaTokenDao plusSaTokenDao(RedisUtil redisUtil) {
        return new PlusSaTokenDao(redisUtil);
    }

    @Bean
    @ConditionalOnMissingBean
    public SaTokenWebMvcConfigurer saTokenWebMvcConfigurer(SecurityProperty properties) {
        return new SaTokenWebMvcConfigurer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public SaTokenExceptionHandler saTokenExceptionHandler(SecurityProperty properties) {
        return new SaTokenExceptionHandler(properties);
    }

    @Bean
    @ConditionalOnMissingBean(StpLogic.class)
    @ConditionalOnProperty(prefix = "sa-token", name = "jwt-secret-key")
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}
