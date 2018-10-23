package com.dozmus.channel;

import com.dozmus.codec.MessageToByteEncoder;
import com.dozmus.codec.ContextualByteToMessageDecoder;
import com.dozmus.codec.MessageCodec;
import com.dozmus.handler.BotClientHandler;
import com.dozmus.handler.ExceptionLogger;
import com.dozmus.handler.IdleHandler;
import com.dozmus.handler.MessageHandler;
import com.dozmus.session.Session;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.Set;

public class BotChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Set<MessageToByteEncoder> encoders;
    private final Set<ContextualByteToMessageDecoder> decoders;
    private final Set<MessageHandler> handlers;
    private final Session session;

    public BotChannelInitializer(Set<MessageToByteEncoder> encoders,
            Set<ContextualByteToMessageDecoder> decoders,
            Set<MessageHandler> handlers, Session session) {
        this.encoders = encoders;
        this.decoders = decoders;
        this.handlers = handlers;
        this.session = session;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new ExceptionLogger()); // downstream exception handler
        ch.pipeline().addLast(new MessageCodec(encoders, decoders, session));
        ch.pipeline().addLast(new IdleHandler());
        ch.pipeline().addLast(new BotClientHandler(session, handlers));
        ch.pipeline().addLast(new ExceptionLogger()); // upstream exception handler
    }
}
