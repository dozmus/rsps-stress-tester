package com.dozmus.codec;

import com.dozmus.message.Message;
import com.dozmus.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;
import java.util.Set;

public class RSMessageCodec extends ByteToMessageCodec<Message> {

    private final Set<ByteToMessageEncoder> encoders;
    private final Set<ContextualByteToMessageDecoder> decoders;
    private final Session session;

    public RSMessageCodec(Set<ByteToMessageEncoder> encoders, Set<ContextualByteToMessageDecoder> decoders,
            Session session) {
        this.encoders = encoders;
        this.decoders = decoders;
        this.session = session;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        try {
            for (ByteToMessageEncoder encoder : encoders) {
                if (encoder.accepts(msg)) {
                    out.writeBytes(encoder.encode(ctx.alloc(), session.getEncrypter(), msg));
                    ctx.writeAndFlush(out);
                    return;
                }
            }
        } finally {
            out.release();
        }
        throw new IllegalArgumentException("Invalid message type: " + msg.getClass().getCanonicalName());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        for (ContextualByteToMessageDecoder decoder : decoders) {
            if (decoder.accepts(session.getState())) {
                decoder.decode(session.getState(), in, out);
                return;
            }
        }
    }

    public Set<ByteToMessageEncoder> getEncoders() {
        return encoders;
    }

    public Set<ContextualByteToMessageDecoder> getDecoders() {
        return decoders;
    }

    public Session getSession() {
        return session;
    }
}
