package com.dozmus.channel;

import com.dozmus.codec.ByteToMessageEncoder;
import com.dozmus.codec.ContextualByteToMessageDecoder;
import com.dozmus.codec.RSMessageCodec;
import com.dozmus.session.Session;
import com.google.inject.Inject;
import io.netty.channel.ChannelHandler;

import java.util.Set;

public class BotChannelInitializerFactory {

    private final Set<ByteToMessageEncoder> encoders;
    private final Set<ContextualByteToMessageDecoder> decoders;

    @Inject
    public BotChannelInitializerFactory(Set<ByteToMessageEncoder> encoders,
            Set<ContextualByteToMessageDecoder> decoders) {
        this.encoders = encoders;
        this.decoders = decoders;
    }

    public ChannelHandler create(Session session) {
        RSMessageCodec codec = new RSMessageCodec(encoders, decoders, session);
        return new BotChannelInitializer(codec, session);
    }
}
