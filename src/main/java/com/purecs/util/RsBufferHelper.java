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

    public static void writeString(ByteBuf buf, String text) {
        for (byte b : text.getBytes()) {
            buf.writeByte(b);
        }
        buf.writeByte(10);
    }

    public static void writeChatString(ByteBuf buf, String text) {
        if (text.length() > 80) {
            text = text.substring(0, 80);
        }
        text = text.toLowerCase();
        int i = -1;

        for (int j = 0; j < text.length(); j++) {
            char c = text.charAt(j);
            int k = 0;

            for (int l = 0; l < VALID_CHARS.length; l++) {
                if (c != VALID_CHARS[l]) {
                    continue;
                }
                k = l;
                break;
            }

            if (k > 12) {
                k += 195;
            }

            if (i == -1) {
                if (k < 13) {
                    i = k;
                } else {
                    buf.writeByte(k);
                }
            } else if (k < 13) {
                buf.writeByte((i << 4) + k);
                i = -1;
            } else {
                buf.writeByte((i << 4) + (k >> 4));
                i = k & 0xf;
            }
        }

        if (i != -1) {
            buf.writeByte(i << 4);
        }
    }

    private static final char[] VALID_CHARS = {
            ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r',
            'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p',
            'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?',
            '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\',
            '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[',
            ']'
    };
}
