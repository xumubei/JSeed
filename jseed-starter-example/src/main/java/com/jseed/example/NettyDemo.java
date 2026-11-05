// SPDX-License-Identifier: Apache-2.0
package com.jseed.example;

import com.jseed.netty.starter.NettyClient;
import com.jseed.netty.starter.NettyServer;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author cyrus
 */
@Profile("netty")
@RestController
@RequestMapping("/demo/netty")
public class NettyDemo {
    private final NettyClient client;
    private final NettyServer server;

    public NettyDemo(NettyClient client, NettyServer server) {
        this.client = client;
        this.server = server;
    }

    @PostMapping
    public String echo(@RequestBody String value) throws Exception {
        if (value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("The demo protocol accepts one line");
        }
        CompletableFuture<String> response = new CompletableFuture<>();
        Channel channel =
                client.connect("127.0.0.1", server.getPort(), initializer(response::complete));
        try {
            channel.writeAndFlush(value + "\n").sync();
            return response.get(5, TimeUnit.SECONDS);
        } finally {
            channel.close();
        }
    }

    static ChannelInitializer<SocketChannel> initializer(Consumer<String> response) {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(SocketChannel channel) {
                channel.pipeline()
                        .addLast(
                                new LineBasedFrameDecoder(8192),
                                new StringDecoder(StandardCharsets.UTF_8),
                                new StringEncoder(StandardCharsets.UTF_8),
                                new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(
                                            ChannelHandlerContext context, String message) {
                                        if (response == null) {
                                            context.writeAndFlush(message + "\n");
                                        } else {
                                            response.accept(message);
                                        }
                                    }
                                });
            }
        };
    }

    @Profile("netty")
    @Configuration(proxyBeanMethods = false)
    static class PipelineConfiguration {
        @Bean("jseedNettyServerInitializer")
        ChannelInitializer<SocketChannel> demoServerInitializer() {
            return initializer(null);
        }
    }
}
