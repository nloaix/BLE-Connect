package com.jht.bleconnect.entity;

import com.jht.bleconnect.common.BaseUtils;

public class SupportedInclinationRange {
    private final String TAG = "SupportedInclinationRange";
    private static SupportedInclinationRange data;

    private byte[] MinimumInclination = new byte[2];
    private byte[] MaximumInclination = new byte[2];
    private byte[] MinimumIncrement = new byte[2];

    public static SupportedInclinationRange getInstance() {
        data = new SupportedInclinationRange();
        return data;
    }

    private SupportedInclinationRange() {
    }

    public void parseData(byte[] buffer) {
        System.arraycopy(buffer, 0, MinimumInclination, 0, 2);
        System.arraycopy(buffer, 2, MaximumInclination, 0, 2);
        System.arraycopy(buffer, 4, MinimumIncrement, 0, 2);
    }

    public double getMinimumInclination() {
        return BaseUtils.bytes2ToInt(MinimumInclination,0) * 0.1;
    }

    public double getMaximumInclination() {
        return BaseUtils.bytes2ToInt(MaximumInclination,0) * 0.1;
    }

    public double getMinimumIncrement() {
        return BaseUtils.bytes2ToInt(MinimumIncrement,0) * 0.1;
    }
}
