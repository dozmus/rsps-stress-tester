package com.dozmus.handler.impl;

import com.dozmus.handler.MessageHandler;
import com.dozmus.message.Message;
import com.dozmus.message.in.JunkMessage;
import com.dozmus.session.LoginState;
import com.dozmus.session.Session;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JunkMessageHandler implements MessageHandler<JunkMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JunkMessageHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, JunkMessage msg) {
        LOGGER.debug("{}: Received junk message.", session.getUsername());
        session.setState(LoginState.WAITING_FOR_RESPONSE_CODE);
    }

    @Override
    public boolean accepts(Message msg) {
        return msg instanceof JunkMessage;
    }
}
