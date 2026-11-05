package com.jseed.mybatis.starter.property;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyBatis-Plus settings
 *
 * @author cyrus
 * @date: 2024-07-12
 */
@Data
@ConfigurationProperties(prefix = "mybatis-plus")
public class MybatisPlusProperty {
    @Schema(description = "分表策略")
    private SplitTableProperty split;

    @Schema(description = "不需要提供的公共拦截器，自己实现 mybatis 拦截器")
    private boolean customInterceptor;

}
