package com.jseed.satoken.starter.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;

import com.jseed.satoken.starter.SecurityProperty;
import com.jseed.satoken.starter.annotation.Anonymous;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author cyrus
 */
public class SaTokenWebMvcConfigurer implements WebMvcConfigurer {
    private final SecurityProperty properties;

    public SaTokenWebMvcConfigurer(SecurityProperty properties) {
        this.properties = properties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(
                        new SaInterceptor(
                                        handler -> {
                                            if (handler instanceof HandlerMethod method
                                                    && !AnnotatedElementUtils.hasAnnotation(
                                                            method.getMethod(), Anonymous.class)
                                                    && !AnnotatedElementUtils.hasAnnotation(
                                                            method.getBeanType(),
                                                            Anonymous.class)) {
                                                StpUtil.checkLogin();
                                            }
                                        })
                                .isAnnotation(true))
                .addPathPatterns("/**")
                .excludePathPatterns(properties.getExcludes());
    }
}
