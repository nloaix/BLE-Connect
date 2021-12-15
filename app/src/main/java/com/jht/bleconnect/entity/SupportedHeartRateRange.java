package com.jht.bleconnect.entity;

import com.jht.bleconnect.common.BaseUtils;

public class SupportedHeartRateRange {
    private final String TAG = "SupportedHeartRateRange";
    private static SupportedHeartRateRange data;

    private byte[] MinimumHeartRate = new byte[1];
    private byte[] MaximumHeartRate = new byte[1];
    private byte[] MinimumIncrement = new byte[1];

    public static SupportedHeartRateRange getInstance() {
        data = new SupportedHeartRateRange();
        return data;
    }

    private SupportedHeartRateRange() {
    }

    public void parseData(byte[] buffer) {
        System.arraycopy(buffer, 0, MinimumHeartRate, 0, 1);
        System.arraycopy(buffer, 1, MaximumHeartRate, 0, 1);
        System.arraycopy(buffer, 2, MinimumIncrement, 0, 1);
    }

    public int getMinimumHeartRate() {
        return BaseUtils.byte1ToInt(MinimumHeartRate[0]);
    }

    public int getMaximumHeartRate() {
        return BaseUtils.byte1ToInt(MaximumHeartRate[0]);
    }

    public int getMinimumIncrement() {
        return BaseUtils.byte1ToInt(MinimumIncrement[0]);
    }

}
