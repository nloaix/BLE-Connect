package com.jht.bleconnect.common;

/**
 * Created by CHARWIN.
 */

public class SetSpeedRequest extends BaseCmdStruct {
    public SetSpeedRequest() {
        super(CmdType.SET_SPEED);
    }

    @CField(start = 10)
    private short speed_x10;

    @CField(start = 12, length = 1)
    private byte metric;

    public short getSpeed_x10() {
        return speed_x10;
    }

    public void setSpeed_x10(short speed_x10) {
        this.speed_x10 = speed_x10;
    }

    public byte getMetric() {
        return metric;
    }

    public void setMetric(byte metric) {
        this.metric = metric;
    }
}

