package com.dozmus.codec.encoder;

import com.dozmus.codec.MessageToByteEncoder;
import com.dozmus.message.Message;
import com.dozmus.message.out.ClientInitMessage;
import com.runescape.ISAACCipher;
import com.runescape.StringHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ClientInitMessageEncoder implements MessageToByteEncoder {

    @Override
    public ByteBuf encode(ByteBufAllocator alloc, ISAACCipher encrypter, Message msg) {
        long name = StringHelper.longForName(((ClientInitMessage) msg).getUsername());
        int nameHash = (int) (name >> 16 & 31L);

        ByteBuf buf = alloc.buffer(2);
        buf.writeByte(14);
        buf.writeByte(nameHash);
        return buf;
    }

    @Override
    public boolean accepts(Message msg) {
        return msg instanceof ClientInitMessage;
    }
}
