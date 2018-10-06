package com.dozmus.codec;

import com.dozmus.session.LoginState;
import io.netty.buffer.ByteBuf;

import java.util.List;

public interface ContextualByteToMessageDecoder {

    void decode(LoginState state, ByteBuf in, List<Object> out) throws Exception;

    boolean accepts(LoginState state);
}
