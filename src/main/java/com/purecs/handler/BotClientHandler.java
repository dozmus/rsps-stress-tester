package com.purecs.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public abstract class BotClientHandler extends ChannelInboundHandlerAdapter {

    public abstract void channelActive(ChannelHandlerContext ctx) throws Exception;

    public abstract void channelInactive(ChannelHandlerContext ctx) throws Exception;

    public abstract void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    public abstract void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    public abstract ByteBuf idlePacket();

    public ByteBuf chatPacket(String text) {
        return chatPacket(text, 0, 0);
    }

    public abstract ByteBuf chatPacket(String text, int effects, int color);
}
