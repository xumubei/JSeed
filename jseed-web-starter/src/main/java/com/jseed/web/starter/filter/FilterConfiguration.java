package com.jseed.web.starter.filter;

import cn.hutool.core.collection.CollectionUtil;
import com.jseed.web.starter.encryption.ApplicationAes;
import com.jseed.web.starter.encryption.aop.ApiEncryptAspect;
import com.jseed.web.starter.property.WebConfigProperty;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.DispatcherType;
import java.util.Map;

/**
 * Filter configuration
 *
 * @author cyrus
 */
public class FilterConfiguration {

    @Bean
    public WebConfigProperty webConfigProperty() {
        return new WebConfigProperty();
    }



    @Bean
    @ConditionalOnProperty(value = "web-config.api-encrypt.enabled", havingValue = "true")
    public ApplicationAes applicationAes(@Autowired WebConfigProperty webConfigProperty) {
        return new ApplicationAes(webConfigProperty.getApiEncrypt().getAesKey());
    }

    @Bean
    @ConditionalOnProperty(value = "web-config.api-encrypt.enabled", havingValue = "true")
    public ApiEncryptAspect apiEncryptAspect(@Autowired WebConfigProperty webConfigProperty, @Autowired ApplicationAes applicationAes) {
        return new ApiEncryptAspect(webConfigProperty.getApiEncrypt(), applicationAes);
    }

    @Bean
    @ConditionalOnProperty(value = "web-config.xss.enabled", havingValue = "true")
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(@Autowired WebConfigProperty webConfigProperty) {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<XssFilter>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns(StringUtils.split(webConfigProperty.getXss().getUrlPatterns(), ","));
        registration.setName("xssFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        Map<String, String> initParameters = Maps.newHashMap();
        initParameters.put("excludes", webConfigProperty.getXss().getExcludes());
        registration.setInitParameters(initParameters);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<SecurityResponseHeaderFilter> responseHeaderFilter() {
        FilterRegistrationBean<SecurityResponseHeaderFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SecurityResponseHeaderFilter());
        registration.addUrlPatterns("/*");
        registration.setName("responseHeaderFilter");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }

    @Bean
    @ConditionalOnProperty(value = "jseed.web.repeatable-body-enabled", havingValue = "true")
    public FilterRegistrationBean<RepeatableFilter> someFilterRegistration() {
        FilterRegistrationBean<RepeatableFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setName("repeatableFilter");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }

    @Bean
    @ConditionalOnProperty(value = "jseed.web.cors-enabled", havingValue = "true")
    public CorsFilter corsFilter(@Autowired WebConfigProperty property) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // Configure allowed origins.
        if (CollectionUtil.isEmpty(property.getAllowedOrigins())) {
            throw new IllegalArgumentException("web-config.allowed-origins must be configured when CORS is enabled");
        } else {
            config.setAllowedOriginPatterns(property.getAllowedOrigins());
        }
        // Configure allowed request headers.
        config.addAllowedHeader("*");
        // Configure allowed HTTP methods.
        config.addAllowedMethod("*");
        // Cache the preflight response for 1800 seconds.
        config.setMaxAge(1800L);
        // Apply the mapping to all request paths.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // Return a new CorsFilter.
        return new CorsFilter(source);
    }
}
