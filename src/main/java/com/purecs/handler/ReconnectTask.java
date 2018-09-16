package com.purecs.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.Random;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ReconnectTask implements Runnable, ChannelFutureListener {

    // Source: https://stackoverflow.com/a/36422221

    private static final Random RANDOM = new Random();
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
            previous.eventLoop().schedule(this, 1000 + RANDOM.nextInt(1000), MILLISECONDS);
        }
    }
}
