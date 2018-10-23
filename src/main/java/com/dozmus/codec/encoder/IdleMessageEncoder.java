package com.dozmus.codec.encoder;

import com.dozmus.codec.MessageToByteEncoder;
import com.dozmus.message.Message;
import com.dozmus.message.out.IdleMessage;
import com.runescape.ISAACCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class IdleMessageEncoder implements MessageToByteEncoder {

    @Override
    public ByteBuf encode(ByteBufAllocator alloc, ISAACCipher encrypter, Message msg) {
        ByteBuf buf = alloc.buffer(1);
        buf.writeByte(encrypter.getNextKey());
        return buf;
    }

    @Override
    public boolean accepts(Message msg) {
        return msg instanceof IdleMessage;
    }
}
