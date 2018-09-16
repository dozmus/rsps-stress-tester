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

    public abstract ByteBuf chatPacket(int effects, int color, String text);
}
