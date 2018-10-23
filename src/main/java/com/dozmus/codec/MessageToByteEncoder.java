package com.dozmus.codec;

import com.runescape.ISAACCipher;
import io.netty.buffer.ByteBuf;
import com.dozmus.message.Message;
import io.netty.buffer.ByteBufAllocator;

public interface MessageToByteEncoder {

    ByteBuf encode(ByteBufAllocator alloc, ISAACCipher encrypter, Message msg) throws Exception;

    boolean accepts(Message msg);
}
