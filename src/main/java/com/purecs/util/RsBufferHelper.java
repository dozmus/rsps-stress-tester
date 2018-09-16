package com.purecs.util;

import io.netty.buffer.ByteBuf;

/**
 * @see <a href="https://github.com/PureCS/rs317-client">rs317-client</a>
 */
public final class RsBufferHelper {

    public static long readULong(ByteBuf buf) {
        long val1 = buf.readUnsignedInt() & 0xffffffffL;
        long val2 = buf.readUnsignedInt() & 0xffffffffL;
        return (val1 << 32) + val2;
    }

    public static void writeString(ByteBuf buf, String name) {
        for (byte b : name.getBytes()) {
            buf.writeByte(b);
        }
        buf.writeByte(10);
    }
}
