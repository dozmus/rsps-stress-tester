package com.dozmus.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionLogger extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLogger.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught", cause);
    }
}
