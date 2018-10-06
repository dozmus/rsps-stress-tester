package com.dozmus.util;

public class ArrayHelper {

    public static int[] incrementValues(int[] a, int d) {
        int[] out = new int[a.length];

        for (int i = 0; i < a.length; i++) {
            out[i] = a[i] + d;
        }
        return out;
    }
}
