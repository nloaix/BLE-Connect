package com.jht.bleconnect.common;

import android.content.Context;

public class BaseUtils {
    public static int byte1ToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * convert byte to int, this method can handle the order (low bit before high bit)
     */
    public static int bytes2ToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset + 1] & 0xFF) << 8)
                | (src[offset] & 0xFF));
        return value;
    }

    public static int byte3ToInt(byte[] bytes, int off) {
        int b0 = bytes[off + 2] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off] & 0xFF;
        return (b0 << 16) | (b1 << 8) | b2;
    }

    public static int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    public static int unsignedBytesToInt(byte lsb, byte msb) {
        return (unsignedByteToInt(lsb) + (unsignedByteToInt(msb) << 8));
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int HL_byte2ToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 8)
                | (src[offset+1] & 0xFF));
        return value;
    }
}

