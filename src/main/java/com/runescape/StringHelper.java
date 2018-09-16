package com.runescape;

/**
 * String utility methods.
 *
 * @see <a href="https://github.com/PureCS/rs317-client">rs317-client</a>
 */
public final class StringHelper {

    private static final char[] VALID_CHARACTERS = {
            '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9'
    };

    public static long longForName(String s) {
        long value = 0L;

        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            value *= 37L;

            if (c >= 'A' && c <= 'Z') {
                value += (1 + c) - 65;
            } else if (c >= 'a' && c <= 'z') {
                value += (1 + c) - 97;
            } else if (c >= '0' && c <= '9') {
                value += (27 + c) - 48;
            }
        }

        for (; value % 37L == 0L && value != 0L; value /= 37L);
        return value;
    }

    public static String nameForLong(long l) {
        try {
            if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L) {
                return "invalid_name";
            }

            if (l % 37L == 0L) {
                return "invalid_name";
            }
            int i = 0;
            char ac[] = new char[12];

            while (l != 0L) {
                long l1 = l;
                l /= 37L;
                ac[11 - i++] = VALID_CHARACTERS[(int) (l1 - l * 37L)];
            }
            return new String(ac, 12 - i, i);
        } catch (RuntimeException ex) {
        }
        throw new RuntimeException();
    }
}
