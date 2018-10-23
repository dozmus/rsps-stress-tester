package com.dozmus.codec.encoder;

import com.dozmus.codec.MessageToByteEncoder;
import com.dozmus.message.Message;
import com.dozmus.message.out.ChannelInitResponseMessage;
import com.dozmus.util.RsBufferHelper;
import com.runescape.ISAACCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ChannelInitResponseEncoder implements MessageToByteEncoder {

    @Override
    public ByteBuf encode(ByteBufAllocator alloc, ISAACCipher encrypter, Message in) {
        ChannelInitResponseMessage msg = (ChannelInitResponseMessage) in;
        int bufSize = msg.getUsername().getBytes().length + 1 + msg.getPassword().getBytes().length + 1
                + 3 + 2 + 1 + 4*8 + 1 + 1 + 4*msg.getKeys().length + 4;

        // Connection settings
        ByteBuf buf = alloc.buffer(bufSize);
        buf.writeByte(msg.isReconnecting() ? 18 : 16);
        buf.writeByte(bufSize - 1);
        buf.writeByte(255); // magic number
        buf.writeShort(317); // client version
        buf.writeByte(msg.isLowMem() ? 1 : 0);

        // cache CRCs
        for (int i = 0; i < 9; i++) {
            buf.writeInt(0);
        }
        buf.writeByte(0); // rsa block length

        // Credentials
        buf.writeByte(10);

        for (int i : msg.getKeys()) {
            buf.writeInt(i);
        }

        // Credentials
        buf.writeInt(msg.getUid()); // uid (generated from cache in this client)
        RsBufferHelper.writeString(buf, msg.getUsername());
        RsBufferHelper.writeString(buf, msg.getPassword());
        return buf;
    }

    @Override
    public boolean accepts(Message msg) {
        return msg instanceof ChannelInitResponseMessage;
    }
}
