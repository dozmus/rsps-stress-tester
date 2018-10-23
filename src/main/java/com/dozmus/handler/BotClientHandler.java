package com.dozmus.handler;

import com.dozmus.handler.task.ReconnectTask;
import com.dozmus.message.Message;
import com.dozmus.message.out.ChatMessage;
import com.dozmus.message.out.ClientInitMessage;
import com.dozmus.message.out.IdleMessage;
import com.dozmus.session.LoginState;
import com.dozmus.session.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class BotClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotClientHandler.class);
    private final Session session;
    private final Set<MessageHandler> handlers;
    private int messageIdx;

    public BotClientHandler(Session session, Set<MessageHandler> handlers) {
        this.session = session;
        this.handlers = handlers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("{}: Connection opened.", session.getUsername());

        // Write login initiation data
        if (session.getState() == LoginState.PENDING_CONNECTION) {
            ctx.write(new ClientInitMessage(session.getUsername()));
            session.setState(LoginState.WAITING_FOR_JUNK);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("{}: Connection lost.", session.getUsername());
        session.setState(LoginState.PENDING_CONNECTION);
        session.setReconnecting(true);

        ReconnectTask reconnect = new ReconnectTask(session.getParent(), ctx.channel());
        reconnect.run();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        for (MessageHandler handler : handlers) {
            if (handler.accepts(msg)) {
                handler.handle(ctx, session, msg);
                break;
            }
        }
    }

    @Override
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
