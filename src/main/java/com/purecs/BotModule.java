package com.purecs;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
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

        bind(SessionCredentialsGenerator.class)
                .to(DefaultSessionCredentialsGenerator.class)
                .in(Singleton.class);
    }
}
