package com.jht.bleconnect.common;


/**
 * Created by CHARWIN.
 */

public enum CmdType {

    // 蓝牙指令
    GET_MACHINE_INFORMATION((byte) 0x02, (byte) 0x16),
    SET_USER_DATA((byte) 0x01, (byte) 0x0F),
    START_WORKOUT((byte) 0x03, (byte) 0x02),
    PAUSE_WORKOUT((byte) 0x03, (byte) 0x03),
    STOP_WORKOUT((byte) 0x02, (byte) 0x14),
    WORKOUT_SNAPSHOT((byte) 0x01, (byte) 0x12),
    INDOORCYCLE_HEARTBEAT_PACKAGE((byte) 0x02, (byte) 0x50),
    SET_SPEED((byte) 0x03, (byte) 0x05),
    SET_INCLINE((byte) 0x03, (byte) 0x06),
    SET_RESISTANCE((byte) 0x03, (byte) 0x07),
    START_ADC_MEASUREMENT((byte) 0x02, (byte) 0x51),
    GET_ADC_VALUE((byte) 0x02, (byte) 0x52),
    GET_WORKOUT_DATA((byte) 0x02, (byte) 0x17),

    // 网络接口指令
    NET_DAPI_LOCATION((byte) 0x81, (byte) 0x03),
    NET_MACHINE_REGISTRATION((byte) 0x81, (byte) 0x02),
    NET_UPDATE_MACHINE_INFO((byte) 0x81, (byte) 0x07),
    NET_UPDATE_MACHINE_STATS((byte) 0x81, (byte) 0x08),
    NET_WORKOUT_DATA_SNAPSHOT((byte) 0x80, (byte) 0x06),
    NET_WORKOUT_COMPLETE((byte) 0x80, (byte) 0x07),
    NET_WORKOUT_COMPLETE_SPRINT_8((byte) 0x80, (byte) 0x08),
    NET_WORKOUT_COMPLETE_FITNESS_TEST((byte) 0x80, (byte) 0x09),
    NET_CURRENT_TIME_REQUEST((byte) 0x82, (byte) 0x01),
    NET_USER_SYNC((byte) 0x83, (byte) 0x01),
    NET_USER_LOGIN((byte) 0x83, (byte) 0x02),
    NET_USER_REGISTER((byte) 0x83, (byte) 0x03),
    NET_CHECK_XID((byte) 0x83, (byte) 0x04),
    NET_CLEAR_USER_INFO((byte) 0x83, (byte) 0x05),
    NET_UNLINK_USER((byte) 0x83, (byte) 0x0A),
    NET_RESET_PASSCODE((byte) 0x83, (byte) 0x06),
    NET_CHECK_UNIQUE_EMAIL((byte) 0x83, (byte) 0x10);


    private byte lingoId;
    private byte messageId;

    private CmdType(byte lingoId, byte messageId) {
        this.lingoId = lingoId;
        this.messageId = messageId;
    }


    public byte getLingoId() {
        return lingoId;
    }

    public byte getMessageId() {
        return messageId;
    }
}

