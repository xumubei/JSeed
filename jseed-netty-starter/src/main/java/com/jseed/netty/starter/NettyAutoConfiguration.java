// SPDX-License-Identifier: Apache-2.0
package com.jseed.netty.starter;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author cyrus
 */
@AutoConfiguration
@EnableConfigurationProperties(NettyProperties.class)
@ConditionalOnProperty(prefix = "jseed.netty", name = "enabled", havingValue = "true")
public class NettyAutoConfiguration {
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public NettyClient nettyClient(NettyProperties properties) {
        return new NettyClient(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "jseed.netty", name = "server-enabled", havingValue = "true")
    public NettyServer nettyServer(
            NettyProperties properties,
            @Qualifier("jseedNettyServerInitializer")
                    ChannelInitializer<SocketChannel> initializer) {
        return new NettyServer(properties, initializer);
    }
}
