// SPDX-License-Identifier: Apache-2.0
package com.jseed.netty.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.springframework.context.SmartLifecycle;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Protocol codecs and handlers are supplied by the application.
 *
 * @author cyrus
 */
public class NettyServer implements SmartLifecycle {
    private final NettyProperties properties;
    private final ChannelInitializer<SocketChannel> initializer;
    private EventLoopGroup boss, workers;
    private volatile Channel channel;

    public NettyServer(NettyProperties p, ChannelInitializer<SocketChannel> initializer) {
        properties = p;
        this.initializer = initializer;
    }

    @Override
    public synchronized void start() {
        if (isRunning()) return;
        if (properties.getWorkerThreads() < 1
                || properties.getPort() < 0
                || properties.getPort() > 65535)
            throw new IllegalArgumentException("Invalid Netty port/thread limits");
        boss = new NioEventLoopGroup(1);
        workers = new NioEventLoopGroup(properties.getWorkerThreads());
        try {
            ChannelFuture binding =
                    new ServerBootstrap()
                            .group(boss, workers)
                            .channel(NioServerSocketChannel.class)
                            .childOption(ChannelOption.TCP_NODELAY, true)
                            .childHandler(initializer)
                            .bind(properties.getHost(), properties.getPort())
                            .awaitUninterruptibly();
            if (!binding.isSuccess()) {
                throw new IllegalStateException("Cannot bind Netty server", binding.cause());
            }
            channel = binding.channel();
        } catch (RuntimeException error) {
            stop();
            throw error;
        }
    }

    public int getPort() {
        if (!isRunning()) throw new IllegalStateException("Netty server is not running");
        return ((InetSocketAddress) channel.localAddress()).getPort();
    }

    @Override
    public synchronized void stop() {
        if (channel != null) {
            channel.close().syncUninterruptibly();
            channel = null;
        }
        if (workers != null) {
            workers.shutdownGracefully(0, 5, TimeUnit.SECONDS).syncUninterruptibly();
            workers = null;
        }
        if (boss != null) {
            boss.shutdownGracefully(0, 5, TimeUnit.SECONDS).syncUninterruptibly();
            boss = null;
        }
    }

    @Override
    public boolean isRunning() {
        return channel != null && channel.isActive();
    }
}
