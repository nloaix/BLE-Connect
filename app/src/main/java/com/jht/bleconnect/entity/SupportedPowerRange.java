package com.jht.bleconnect.entity;

import com.jht.bleconnect.common.BaseUtils;

public class SupportedPowerRange {
    private final String TAG = "SupportedPowerRange";
    private static SupportedPowerRange data;

    private byte[] MinimumPower = new byte[2];
    private byte[] MaximumPower = new byte[2];
    private byte[] MinimumIncrement = new byte[2];

    public static SupportedPowerRange getInstance() {
        data = new SupportedPowerRange();
        return data;
    }

    private SupportedPowerRange() {
    }

    public void parseData(byte[] buffer) {
        System.arraycopy(buffer, 0, MinimumPower, 0, 2);
        System.arraycopy(buffer, 2, MaximumPower, 0, 2);
        System.arraycopy(buffer, 4, MinimumIncrement, 0, 2);
    }

    public double getMinimumPower() {
        return BaseUtils.bytes2ToInt(MinimumPower,0) ;
    }

    public double getMaximumPower() {
        return BaseUtils.bytes2ToInt(MaximumPower,0) ;
    }

    public double getMinimumIncrement() {
        return BaseUtils.bytes2ToInt(MinimumIncrement,0);
    }
}
