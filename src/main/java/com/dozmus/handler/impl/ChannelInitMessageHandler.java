package com.dozmus.handler.impl;

import com.dozmus.handler.MessageHandler;
import com.dozmus.message.Message;
import com.dozmus.message.in.ChannelInitMessage;
import com.dozmus.message.out.ChannelInitResponseMessage;
import com.dozmus.session.LoginState;
import com.dozmus.session.Session;
import com.dozmus.util.ArrayHelper;
import com.runescape.ISAACCipher;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelInitMessageHandler implements MessageHandler<ChannelInitMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelInitMessageHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, Session session, ChannelInitMessage msg) {
        if (msg.getResponseCode() != 0) {
            LOGGER.info("{}: Bad response code: {}", session.getUsername(), msg.getResponseCode());
            session.setState(LoginState.DISCONNECTED);
            return;
        } else {
            LOGGER.debug("{}: Response code: {}", session.getUsername(), msg.getResponseCode());
        }

        // Generate csk/ssk key-pair
        int encryptKeys[] = new int[4];
        encryptKeys[0] = (int) (Math.random() * 99999999D);
        encryptKeys[1] = (int) (Math.random() * 99999999D);
        encryptKeys[2] = (int) (msg.getServerSessionKey() >> 32);
        encryptKeys[3] = (int) msg.getServerSessionKey();

        // Seed encrypter/decrypter
        session.setEncrypter(new ISAACCipher(encryptKeys));
        int[] decryptKeys = ArrayHelper.incrementValues(encryptKeys, 50);
        session.setDecrypter(new ISAACCipher(decryptKeys));

        ctx.write(new ChannelInitResponseMessage(encryptKeys, session.getUsername(),
                session.getPassword(), session.getUid(), session.isReconnecting(), session.isLowMem()));
        session.setState(LoginState.WAITING_FOR_LOGIN_RESPONSE);
    }

    @Override
    public boolean accepts(Message msg) {
        return msg instanceof ChannelInitMessage;
    }
}
