package com.purecs.handler;

import com.purecs.util.RsBufferHelper;
import com.runescape.ISAACCipher;
import com.runescape.StringHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotClientHandler317 extends ChannelInboundHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BotClientHandler317.class);
    private static final int RESPONSE_OK = 2;
    private String name;
    private Bootstrap bootstrap;
    private LoginState state = LoginState.PENDING_CONNECTION;
    private boolean reconnecting  = false;
    private boolean lowMem = true;
    private ISAACCipher encrypter, decrypter;

    public BotClientHandler317(String name, Bootstrap bootstrap) {
        this.name = name;
        this.bootstrap = bootstrap;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("{}: Connection opened.", name);

        // Write login initiation data
        if (state == LoginState.PENDING_CONNECTION) {
            ByteBuf buf = Unpooled.buffer(2);
            long name = StringHelper.longForName(this.name);
            int nameHash = (int) (name >> 16 & 31L);
            buf.writeByte(14);
            buf.writeByte(nameHash);
            ctx.writeAndFlush(buf);
            setState(LoginState.WAITING_FOR_JUNK);
        }
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.info("{}: Connection lost.", name);
        setState(LoginState.PENDING_CONNECTION);
        reconnecting = true;

        ReconnectTask reconnect = new ReconnectTask(bootstrap, ctx.channel());
        reconnect.run();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf)msg;

        if (getState() == LoginState.WAITING_FOR_JUNK) {
            if (buf.readableBytes() >= 8) {
                buf.readBytes(8);
                setState(LoginState.WAITING_FOR_RESPONSE_CODE);
            }
        }

        if (getState() == LoginState.WAITING_FOR_RESPONSE_CODE) {
            if (buf.readableBytes() >= 1) {
                int responseCode = buf.readByte();

                if (responseCode != 0) {
                    LOGGER.info("{}: Bad response code: {}", name, responseCode);
                    setState(LoginState.DISCONNECTED);
                    return;
                } else {
                    LOGGER.info("{}: Response code: {}", name, responseCode);
                }

                // generate csk,ssk pair
                long serverSessionKey = RsBufferHelper.readULong(buf);
                int ai[] = new int[4];
                ai[0] = (int) (Math.random() * 99999999D);
                ai[1] = (int) (Math.random() * 99999999D);
                ai[2] = (int) (serverSessionKey >> 32);
                ai[3] = (int) serverSessionKey;

                // rest
                ByteBuf response = Unpooled.buffer();
                response.writeByte(10);

                for (int i : ai) {
                    response.writeInt(i);
                }
                response.writeInt(27738603); // uid (generated from cache in this client)
                RsBufferHelper.writeString(response, name); // name
                RsBufferHelper.writeString(response, "test123"); // pwd
                // TODO generate keys?

                ByteBuf response2 = Unpooled.buffer();
                response2.writeByte(reconnecting ? 18 : 16);
                response2.writeByte(response.arrayOffset() + 36 + 1 + 1 + 2);
                response2.writeByte(255);
                response2.writeShort(317);
                response2.writeByte(lowMem ? 1 : 0);

                // cache CRCs
                for (int i = 0; i < 9; i++) {
                    response2.writeInt(0);
                }
                response2.writeByte(0); // rsa block length
                response2.writeBytes(response);

                // Setup isaac
                encrypter = new ISAACCipher(ai);

                for (int i = 0; i < 4; i++) {
                    ai[i] += 50;
                }
                decrypter = new ISAACCipher(ai);
                ctx.writeAndFlush(response2);
                setState(LoginState.WAITING_FOR_LOGIN_RESPONSE);
            }
        }

        if (getState() == LoginState.WAITING_FOR_LOGIN_RESPONSE) {
            if (buf.readableBytes() > 3) {
                int resp = buf.readByte();

                if (resp == RESPONSE_OK) {
                    int privilege = buf.readByte();
                    boolean flagged = buf.readByte() == 1;
                    // other flags and setup IDK shit
                    setState(LoginState.CONNECTED);
                } else {
                    setState(LoginState.DISCONNECTED);
                }
            }
        }
        buf.release();
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent && getState() == LoginState.CONNECTED) {
            ctx.writeAndFlush(idlePacket());
        }
    }

    public ByteBuf idlePacket() {
        ByteBuf buf = Unpooled.buffer(1);
        buf.writeByte(encrypter.getNextKey());
        return buf;
    }

    public ByteBuf chatPacket(int effects, int color, String text) { // does this work
        int packetSize = 2 + 1 + text.getBytes().length;
        ByteBuf buf = Unpooled.buffer(1 + packetSize);
        buf.writeByte(encrypter.getNextKey() + 4);
        buf.writeByte(packetSize);
        buf.writeByte(128 - effects);
        buf.writeByte(128 - color);
        byte[] b = text.getBytes();

        for (int i = b.length - 1; i >= 0; i--) {
            buf.writeByte(b[i] + 128);
        }
        buf.writeByte(10);
        return buf;
    }

    public LoginState getState() {
        return state;
    }

    public void setState(LoginState state) {
        this.state = state;
    }

    public enum LoginState {
        PENDING_CONNECTION,
        WAITING_FOR_JUNK,
        WAITING_FOR_RESPONSE_CODE,
        WAITING_FOR_LOGIN_RESPONSE,
        CONNECTED,
        DISCONNECTED
    }
}
