package com.dozmus.codec.decoder;

import com.dozmus.codec.ContextualByteToMessageDecoder;
import com.dozmus.message.in.ChannelInitMessage;
import com.dozmus.message.in.JunkMessage;
import com.dozmus.message.in.LoginResponseMessage;
import com.dozmus.session.LoginState;
import com.dozmus.util.RsBufferHelper;
import io.netty.buffer.ByteBuf;

import java.util.List;

public class LoginDecoder implements ContextualByteToMessageDecoder {

    public static final int HANDSHAKE_RESPONSE_OK = 2;

    @Override
    public void decode(LoginState state, ByteBuf in, List<Object> out) throws Exception {
        switch (state) {
            case WAITING_FOR_JUNK:
                if (in.readableBytes() >= 8) {
                    in.readBytes(8);
                    out.add(new JunkMessage());
                }
                break;
            case WAITING_FOR_LOGIN_RESPONSE:
                if (waitingForLoginResponseReady(in)) {
                    int resp = in.readByte();
                    int privilege = -1;
                    boolean flagged = false;

                    if (resp == HANDSHAKE_RESPONSE_OK) {
                        privilege = in.readByte();
                        flagged = in.readByte() == 1;
                    }
                    out.add(new LoginResponseMessage(resp, privilege, flagged));
                }
                break;
            case WAITING_FOR_RESPONSE_CODE:
                if (waitingForResponseReady(in)) {
                    int responseCode = in.readByte();
                    long serverSessionKey = responseCode == 0 ? RsBufferHelper.readULong(in) : -1;
                    out.add(new ChannelInitMessage(responseCode, serverSessionKey));
                }
                break;
        }
    }

    private boolean waitingForLoginResponseReady(ByteBuf in) {
        int i = in.readerIndex();
        return in.readableBytes() >= 1 && (in.getByte(i) != HANDSHAKE_RESPONSE_OK || in.readableBytes() >= 3);
    }

    private boolean waitingForResponseReady(ByteBuf in) {
        int i = in.readerIndex();
        return in.readableBytes() >= 1 && (in.getByte(i) != 0 || in.readableBytes() >= 9);
    }

    @Override
    public boolean accepts(LoginState state) {
        return state == LoginState.WAITING_FOR_JUNK
                || state == LoginState.WAITING_FOR_LOGIN_RESPONSE
                || state == LoginState.WAITING_FOR_RESPONSE_CODE;
    }
}
