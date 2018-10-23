package com.dozmus.codec.encoder;

import com.dozmus.codec.MessageToByteEncoder;
import com.dozmus.message.Message;
import com.dozmus.message.out.ChatMessage;
import com.dozmus.util.RsBufferHelper;
import com.runescape.ISAACCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ChatMessageEncoder implements MessageToByteEncoder {

    @Override
    public ByteBuf encode(ByteBufAllocator alloc, ISAACCipher encrypter, Message in) {
        ChatMessage msg = (ChatMessage) in;

        ByteBuf msgBuf = alloc.buffer(msg.getText().length());
        RsBufferHelper.writeChatString(msgBuf, msg.getText());
        int bufSize = 4 + msgBuf.writerIndex();

        ByteBuf buf = alloc.buffer(bufSize);
        buf.writeByte(encrypter.getNextKey() + 4);
        buf.writeByte(bufSize - 2);
        buf.writeByte(128 - msg.getEffects());
        buf.writeByte(128 - msg.getColor());

        msgBuf.forEachByteDesc(value -> {
            buf.writeByte(value + 128);
            return true;
        });
        return buf;
    }

    @Override
    public boolean accepts(Message msg) {
        return msg instanceof ChatMessage;
    }
}
