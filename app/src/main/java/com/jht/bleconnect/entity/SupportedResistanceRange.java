package com.jht.bleconnect.entity;

import com.jht.bleconnect.common.BaseUtils;


public class SupportedResistanceRange {


    private final String TAG = "SupportedResistanceRange";
    private static SupportedResistanceRange data;

    private byte[] MinimumResistanceLevel = new byte[2];
    private byte[] MaximumResistanceLevel = new byte[2];
    private byte[] MinimumIncrement = new byte[2];

    public static SupportedResistanceRange getInstance() {
        data = new SupportedResistanceRange();
        return data;
    }

    private SupportedResistanceRange() {
    }

    public void parseData(byte[] buffer) {
        System.arraycopy(buffer, 0, MinimumResistanceLevel, 0, 2);
        System.arraycopy(buffer, 2, MaximumResistanceLevel, 0, 2);
        System.arraycopy(buffer, 4, MinimumIncrement, 0, 2);
    }

    public double getMinimumResistanceLevel() {
        return BaseUtils.bytes2ToInt(MinimumResistanceLevel,0) * 0.1;
    }

    public double getMaximumResistanceLevel() {
        return BaseUtils.bytes2ToInt(MaximumResistanceLevel,0) * 0.1;
    }

    public double getMinimumIncrement() {
        return BaseUtils.bytes2ToInt(MinimumIncrement,0) * 0.1;
    }
}
