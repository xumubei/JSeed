// SPDX-License-Identifier: Apache-2.0
package com.jseed.rabbitmq.starter;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Failed messages are rejected to the queue's configured dead-letter exchange.
 *
 * @author cyrus
 */
@AutoConfiguration(
        afterName = "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration")
@EnableConfigurationProperties(RabbitProperties.class)
@ConditionalOnProperty(prefix = "jseed.rabbitmq", name = "enabled", havingValue = "true")
public class RabbitAutoConfiguration {
    @Bean(name = "jseedRabbitListenerContainerFactory")
    @ConditionalOnMissingBean(name = "jseedRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory listenerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connection,
            RabbitProperties p) {
        if (p.getPrefetch() < 1 || p.getConcurrentConsumers() < 1 || p.getMaxAttempts() < 1)
            throw new IllegalArgumentException("Invalid Rabbit listener limits");
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connection);
        factory.setPrefetchCount(p.getPrefetch());
        factory.setConcurrentConsumers(p.getConcurrentConsumers());
        factory.setDefaultRequeueRejected(false);
        if (p.getMaxAttempts() > 1) {
            factory.setAdviceChain(
                    RetryInterceptorBuilder.stateless()
                            .maxRetries(p.getMaxAttempts() - 1)
                            .backOffOptions(1000, 2.0, 10000)
                            .recoverer(new RejectAndDontRequeueRecoverer())
                            .build());
        }
        return factory;
    }
}
