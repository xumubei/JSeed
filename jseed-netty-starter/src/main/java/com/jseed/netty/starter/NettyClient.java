// SPDX-License-Identifier: Apache-2.0
package com.jseed.netty.starter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Reuses event loops; tracks and closes connections it creates.
 *
 * @author cyrus
 */
public class NettyClient implements AutoCloseable {
    private final NettyProperties properties;
    private EventLoopGroup workers;
    private final Set<Channel> channels = ConcurrentHashMap.newKeySet();
    private boolean closed;

    public NettyClient(NettyProperties properties) {
        if (properties.getWorkerThreads() < 1 || properties.getConnectTimeoutMillis() < 1)
            throw new IllegalArgumentException("Invalid Netty thread/timeout limits");
        this.properties = properties;
    }

    public synchronized Channel connect(
            String host, int port, ChannelInitializer<SocketChannel> initializer) {
        if (closed) throw new IllegalStateException("Netty client is closed");
        if (workers == null) workers = new NioEventLoopGroup(properties.getWorkerThreads());
        Channel channel =
                new Bootstrap()
                        .group(workers)
                        .channel(NioSocketChannel.class)
                        .option(
                                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                                properties.getConnectTimeoutMillis())
                        .handler(initializer)
                        .connect(host, port)
                        .syncUninterruptibly()
                        .channel();
        channels.add(channel);
        channel.closeFuture().addListener(future -> channels.remove(channel));
        return channel;
    }

    @Override
    public synchronized void close() {
        closed = true;
        for (Channel channel : channels) channel.close().syncUninterruptibly();
        channels.clear();
        if (workers != null) {
            workers.shutdownGracefully(0, 5, TimeUnit.SECONDS).syncUninterruptibly();
            workers = null;
        }
    }
}
