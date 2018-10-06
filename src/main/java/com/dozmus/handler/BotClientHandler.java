package com.dozmus.handler;

import com.dozmus.codec.decoder.LoginDecoder;
import com.dozmus.handler.task.ReconnectTask;
import com.dozmus.message.in.ChannelInitMessage;
import com.dozmus.message.in.LoginResponseMessage;
import com.dozmus.message.in.JunkMessage;
import com.dozmus.message.out.ChannelInitResponseMessage;
import com.dozmus.message.out.ChatMessage;
import com.dozmus.message.out.ClientInitMessage;
import com.dozmus.message.out.IdleMessage;
import com.dozmus.session.LoginState;
import com.dozmus.session.Session;
import com.dozmus.util.ArrayHelper;
import com.runescape.ISAACCipher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotClientHandler.class);
    private final Session session;
    private int messageIdx;

    public BotClientHandler(Session session) {
        this.session = session;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("{}: Connection opened.", session.getUsername());

        // Write login initiation data
        if (session.getState() == LoginState.PENDING_CONNECTION) {
            ctx.write(new ClientInitMessage(session.getUsername()));
            session.setState(LoginState.WAITING_FOR_JUNK);
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("{}: Connection lost.", session.getUsername());
        session.setState(LoginState.PENDING_CONNECTION);
        session.setReconnecting(true);

        ReconnectTask reconnect = new ReconnectTask(session.getParent(), ctx.channel());
        reconnect.run();
    }

    public void channelRead(ChannelHandlerContext ctx, Object in) {
        // TODO we would use observer, or pub-sub, if we want to start doing interesting things
        if (in instanceof JunkMessage) {
            LOGGER.debug("{}: Received junk message.", session.getUsername());
            session.setState(LoginState.WAITING_FOR_RESPONSE_CODE);
        }

        if (in instanceof ChannelInitMessage) {
            ChannelInitMessage msg = (ChannelInitMessage) in;

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

        if (in instanceof LoginResponseMessage) {
            LoginResponseMessage msg = (LoginResponseMessage) in;
            LOGGER.info("{}: Response code: {}", session.getUsername(), msg.getResponseCode());

            if (msg.getResponseCode() == LoginDecoder.HANDSHAKE_RESPONSE_OK) {
                session.setState(LoginState.CONNECTED);
            } else {
                session.setState(LoginState.DISCONNECTED);
            }
        }
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && session.getState() == LoginState.CONNECTED) {
            ctx.write(new IdleMessage());

            if (!session.getMessages().isEmpty()) {
                String message = session.getMessages().get(messageIdx);
                ctx.write(new ChatMessage(message));
                messageIdx = (messageIdx + 1) % session.getMessages().size();
            }
        }
    }
}
