package com.dozmus.channel;

import com.dozmus.codec.MessageToByteEncoder;
import com.dozmus.codec.ContextualByteToMessageDecoder;
import com.dozmus.handler.MessageHandler;
import com.dozmus.session.Session;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;

import java.util.Set;

public class BotChannelInitializerFactory {

    private final Set<MessageToByteEncoder> encoders;
    private final Set<ContextualByteToMessageDecoder> decoders;
    private final Set<MessageHandler> handlers;

    @Inject
    public BotChannelInitializerFactory(Set<MessageToByteEncoder> encoders,
            Set<ContextualByteToMessageDecoder> decoders, Set<MessageHandler> handlers) {
        this.encoders = encoders;
        this.decoders = decoders;
        this.handlers = handlers;
    }

    public ChannelHandler create(Session session) {
        return new BotChannelInitializer(encoders, decoders, handlers, session);
    }
}
