package com.jht.bleconnect.common;

public class SetInclineRequest extends BaseCmdStruct {
    public SetInclineRequest() {
        super(CmdType.SET_INCLINE);
    }

    @CField(start = 10)
    private short incline_x10;

    public short getIncline_x10() {
        return incline_x10;
    }

    public void setIncline_x10(short incline_x10) {
        this.incline_x10 = incline_x10;
    }
}
