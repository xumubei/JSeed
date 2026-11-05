// SPDX-License-Identifier: Apache-2.0
package com.jseed.swagger.starter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cyrus
 */
@AutoConfiguration
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(
        prefix = "jseed.swagger",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class SwaggerAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI jseedOpenApi(SwaggerProperties p) {
        return new OpenAPI()
                .info(
                        new Info()
                                .title(p.getTitle())
                                .description(p.getDescription())
                                .version(p.getVersion()));
    }
}
