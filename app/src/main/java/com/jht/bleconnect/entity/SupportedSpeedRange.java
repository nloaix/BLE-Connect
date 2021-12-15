package com.jht.bleconnect.entity;

import com.jht.bleconnect.common.BaseUtils;

public class SupportedSpeedRange {
    private final String TAG = "SupportedSpeedRange";
    private static SupportedSpeedRange data;

    private byte[] MinimumSpeed = new byte[2];
    private byte[] MaximumSpeed = new byte[2];;
    private byte[] MinimumIncrement = new byte[2];;

    public static SupportedSpeedRange getInstance()
    {
        data = new SupportedSpeedRange();
        return data;
    }
    private SupportedSpeedRange(){}

    public void parseData(byte[] buffer)
    {
        System.arraycopy(buffer, 0, MinimumSpeed, 0, 2);
        System.arraycopy(buffer, 2, MaximumSpeed, 0, 2);
        System.arraycopy(buffer, 4, MinimumIncrement, 0, 2);
    }

    public double getMinimumSpeed() {
        return BaseUtils.bytes2ToInt(MinimumSpeed,0) * 0.01;
    }

    public double getMaximumSpeed() {
        return BaseUtils.bytes2ToInt(MaximumSpeed,0) * 0.01;
    }

    public double getMinimumIncrement() {
        return BaseUtils.bytes2ToInt(MinimumIncrement,0) * 0.01;
    }
}
