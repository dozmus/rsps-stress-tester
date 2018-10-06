package com.dozmus.handler;

import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public final class IdleHandler extends IdleStateHandler {

    public IdleHandler() {
        super(0, 600, 0, TimeUnit.MILLISECONDS);
    }
}
