package com.purecs;

import com.purecs.handler.IdleHandler;
import com.purecs.handler.BotClientHandler317;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class BotClient {

    private final Bootstrap bootstrap = new Bootstrap();

    public BotClient(int cores) {
        EventLoopGroup workerGroup = new NioEventLoopGroup(cores);
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public BotClient() {
        this(1);
    }

    public void connect(String host, int port, String name) {
        // Configure client
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new IdleHandler());
                    ch.pipeline().addLast(new BotClientHandler317(name, bootstrap));
                    // https://stackoverflow.com/questions/7895964/how-does-the-netty-threading-model-work-in-the-case-of-many-client-connections/7905761#7905761
                }
            });

        // Connect to server
        bootstrap.connect(host, port);
    }
}
