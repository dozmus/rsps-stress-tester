package com.dozmus.handler.task;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.ThreadLocalRandom;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public final class ReconnectTask implements Runnable, ChannelFutureListener {

    // Source: https://stackoverflow.com/a/36422221

    private static final int MINIMUM_DELAY_MS = 1_000;
    private static final int MAXIMUM_DELAY_MS = 5_000;
    private final Bootstrap bootstrap;
    private final Channel previous;

    public ReconnectTask(Bootstrap bootstrap, Channel c) {
        this.bootstrap = bootstrap;
        this.previous = c;
    }

    public void run() {
        bootstrap.remoteAddress(previous.remoteAddress())
                .connect()
                .addListener(this);
    }

    public void operationComplete(ChannelFuture future) {
        if (!future.isSuccess()) {
            int delay = ThreadLocalRandom.current().nextInt(MINIMUM_DELAY_MS, MAXIMUM_DELAY_MS);
            previous.eventLoop().schedule(this, delay, MILLISECONDS);
        }
    }
}
