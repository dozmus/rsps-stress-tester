package com.dozmus.channel;

import com.dozmus.codec.RSMessageCodec;
import com.dozmus.handler.BotClientHandler;
import com.dozmus.handler.ExceptionLogger;
import com.dozmus.handler.IdleHandler;
import com.dozmus.session.Session;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class BotChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Session session;
    private final RSMessageCodec codec;

    public BotChannelInitializer(RSMessageCodec codec, Session session) {
        this.codec = codec;
        this.session = session;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new ExceptionLogger()); // downstream handler
        ch.pipeline().addLast(new RSMessageCodec(codec.getEncoders(), codec.getDecoders(), codec.getSession()));
        ch.pipeline().addLast(new IdleHandler());
        ch.pipeline().addLast(new BotClientHandler(session));
        ch.pipeline().addLast(new ExceptionLogger()); // upstream handler
    }
}
