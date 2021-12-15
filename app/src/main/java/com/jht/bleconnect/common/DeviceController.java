package com.jht.bleconnect.common;

public class DeviceController {

    public static byte[] setSpeed(short speed_x10, boolean isMetric) {
        SetSpeedRequest request = new SetSpeedRequest();
        request.setSpeed_x10(speed_x10);
        request.setMetric((byte) (1));
        //sendData(CFieldUtil.getBytes(request));
        byte[] data = CFieldUtil.getBytes(request);
        byte[] result = CmdUtil.mergeBytes(data, new byte[]{0x0d, 0x0a});
        return result;
    }

    public static byte[] setIncline(short incline_x10) {
        SetInclineRequest request = new SetInclineRequest();
        request.setIncline_x10(incline_x10);
        //sendData(CFieldUtil.getBytes(request));
        byte[] data = CFieldUtil.getBytes(request);
        byte[] result = CmdUtil.mergeBytes(data, new byte[]{0x0d, 0x0a});
        return result;
    }
}
