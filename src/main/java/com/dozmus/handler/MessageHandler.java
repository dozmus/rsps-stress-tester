package com.dozmus.handler;

import com.dozmus.message.Message;
import com.dozmus.session.Session;
import io.netty.channel.ChannelHandlerContext;

public interface MessageHandler<M extends Message> {

    void handle(ChannelHandlerContext ctx, Session session, M msg);

    boolean accepts(Message msg);
}
