package com.dozmus.handler.impl;

import com.dozmus.codec.decoder.LoginDecoder;
import com.dozmus.handler.MessageHandler;
import com.dozmus.message.Message;
import com.dozmus.message.in.LoginResponseMessage;
import com.dozmus.session.LoginState;
import com.dozmus.session.Session;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginResponseMessageHandler implements MessageHandler<LoginResponseMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginResponseMessageHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, LoginResponseMessage msg) {
        LOGGER.info("{}: Response code: {}", session.getUsername(), msg.getResponseCode());

        if (msg.getResponseCode() == LoginDecoder.HANDSHAKE_RESPONSE_OK) {
            session.setState(LoginState.CONNECTED);
        } else {
            session.setState(LoginState.DISCONNECTED);
        }
    }

    @Override
    public boolean accepts(Message m) {
        return m instanceof LoginResponseMessage;
    }
}
