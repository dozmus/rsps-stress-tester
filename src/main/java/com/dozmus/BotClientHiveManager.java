package com.dozmus;

import com.dozmus.channel.BotChannelInitializerFactory;
import com.dozmus.session.Session;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.List;

public class BotClientHiveManager {

    private final Bootstrap bootstrap = new Bootstrap();
    private final String host;
    private final int port;
    private final List<String> messages;
    private BotChannelInitializerFactory factory;

    @Inject
    public BotClientHiveManager(@Named("host") String host, @Named("port") int port, @Named("threads") int threads,
                         @Named("messages") List<String> messages, BotChannelInitializerFactory factory) {
        this.host = host;
        this.port = port;
        this.messages = messages;
        this.factory = factory;
        EventLoopGroup workerGroup = new NioEventLoopGroup(threads);
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void connect(String username, String password, int uid) {
        // Configure client
        Session session = new Session(username, password, uid, bootstrap, messages);
        bootstrap.handler(factory.create(session));

        // Connect to server
        bootstrap.connect(host, port);
    }
}
