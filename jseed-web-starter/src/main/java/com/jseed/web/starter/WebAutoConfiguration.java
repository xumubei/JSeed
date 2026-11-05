// SPDX-License-Identifier: Apache-2.0
package com.jseed.web.starter;

import com.jseed.web.starter.filter.FilterConfiguration;
import com.jseed.web.starter.log.OperationLogAspect;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author cyrus
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({
    JacksonSerializer.class,
    FilterConfiguration.class,
    GlobalExceptionHandler.class,
    ResponseReturnHandler.class
})
public class WebAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public OperationLogAspect operationLogAspect(ApplicationEventPublisher events) {
        return new OperationLogAspect(events);
    }
}
