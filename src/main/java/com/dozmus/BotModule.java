package com.dozmus;

import com.dozmus.codec.ByteToMessageEncoder;
import com.dozmus.codec.ContextualByteToMessageDecoder;
import com.dozmus.codec.decoder.LoginDecoder;
import com.dozmus.codec.encoder.ChannelInitResponseEncoder;
import com.dozmus.codec.encoder.ChatMessageEncoder;
import com.dozmus.codec.encoder.ClientInitMessageEncoder;
import com.dozmus.codec.encoder.IdleMessageEncoder;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import java.util.List;

public class BotModule extends AbstractModule {

    private final BotClientContext ctx;

    public BotModule(BotClientContext ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void configure() {
        bind(String.class)
                .annotatedWith(Names.named("host"))
                .toInstance(ctx.getHost());
        bind(Integer.class)
                .annotatedWith(Names.named("port"))
                .toInstance(ctx.getPort());
        bind(Integer.class)
                .annotatedWith(Names.named("threads"))
                .toInstance(ctx.getThreads());
        bind(new TypeLiteral<List<String>>() {})
                .annotatedWith(Names.named("messages"))
                .toInstance(ctx.getMessages());

        // Encoders
        Multibinder<ByteToMessageEncoder> encoderBinder = Multibinder.newSetBinder(binder(),
                ByteToMessageEncoder.class);
        encoderBinder.addBinding().to(ChannelInitResponseEncoder.class);
        encoderBinder.addBinding().to(ClientInitMessageEncoder.class);
        encoderBinder.addBinding().to(IdleMessageEncoder.class);
        encoderBinder.addBinding().to(ChatMessageEncoder.class);

        // Decoders
        Multibinder<ContextualByteToMessageDecoder> decoderBinder = Multibinder.newSetBinder(binder(),
                ContextualByteToMessageDecoder.class);
        decoderBinder.addBinding().to(LoginDecoder.class);
    }
}
