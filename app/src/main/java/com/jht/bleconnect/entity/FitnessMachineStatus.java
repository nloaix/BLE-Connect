package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;

public class FitnessMachineStatus {
    private final String TAG = "FitnessMachineStatus";
    private static FitnessMachineStatus data;
    private byte[] flags;

    public static FitnessMachineStatus getInstance() {
        data = new FitnessMachineStatus();
        return data;
    }

    private FitnessMachineStatus() {
    }

    public void parseData(byte[] buffer) {
        Log.i(TAG, "parseData: buffer.length " + buffer.length);
        flags = new byte[buffer.length];
        System.arraycopy(buffer, 0, flags, 0, buffer.length);
    }

    public String getCurrentTrainingStatus() {
        byte status = flags[0];
        switch (status) {
            case 0x00:
                return "Reserved for Future Use";
            case 0x01:
                return "Reset";
            case 0x02:
                if (flags[1] == 0x01) {
                    return "Fitness Machine Stopped or Paused by the User ; Control Information : Stop";
                }else if(flags[1] == 0x02){
                    return "Fitness Machine Stopped or Paused by the User ; Control Information : Pause";
                }else {
                    return "Fitness Machine Stopped or Paused by the User ; Control Information :Reserved for Future Use";
                }
            case 0x03:
                return "Fitness Machine Stopped by Safety Key";
            case 0x04:
                return "Fitness Machine Started or Resumed by the User";
            case 0x05:
                byte[] data = new byte[2];
                data[0] = flags[1];
                data[1] = flags[2];
                return "Target Speed Changed ; New Target Value is " + BaseUtils.bytes2ToInt(data, 0);
            case 0x06:
                byte[] data6 = new byte[2];
                data6[0] = flags[1];
                data6[1] = flags[2];
                return "Target Incline Changed; New Target Value is " + BaseUtils.bytes2ToInt(data6,0);
            case 0x07:
                return "Target Resistance Level Changed ; New Target Value is " +  BaseUtils.byte1ToInt(flags[1]);
            case 0x08:
                byte[] data8 = new byte[2];
                data8[0] = flags[1];
                data8[1] = flags[2];
                return "Target Power Changed ; New Target Value is " +  BaseUtils.bytes2ToInt(data8,0);
            case 0x09:
                return "Target Heart Rate Changed; New Target Value is " + BaseUtils.byte1ToInt(flags[1]);
            case 0x0a:
                byte[] dataa = new byte[2];
                dataa[0] = flags[1];
                dataa[1] = flags[2];
                return "Targeted Expended Energy Changed; New Target Value is " +  BaseUtils.bytes2ToInt(dataa,0);
            case 0x0b:
                byte[] datab = new byte[2];
                datab[0] = flags[1];
                datab[1] = flags[2];
                return "Targeted Number of Steps Changed; New Target Value is " +  BaseUtils.bytes2ToInt(datab,0);
            case 0x0c:
                byte[] datac = new byte[2];
                datac[0] = flags[1];
                datac[1] = flags[2];
                return "Targeted Number of Strides Changed; New Target Value is " +  BaseUtils.bytes2ToInt(datac,0);
            case 0x0d:
                byte[] datad = new byte[3];
                datad[0] = flags[1];
                datad[1] = flags[2];
                datad[2] = flags[3];
                return "Targeted Distance Changed; New Target Value is " +  BaseUtils.byte3ToInt(datad,0);
            case 0x0e:
                byte[] datae = new byte[2];
                datae[0] = flags[1];
                datae[1] = flags[2];
                return "Targeted Training Time Changed; New Target Value is " +  BaseUtils.bytes2ToInt(datae,0);
            case 0x0f:
                return "Targeted Time in Two Heart Rate Zones Changed;";
            case 0x10:
                return "Targeted Time in Three Heart Rate Zones Changed;";
            case 0x11:
                return "Targeted Time in Five Heart Rate Zones Changed;";
            case 0x12:
                return "Indoor Bike Simulation Parameters Changed;";
            case 0x13:
                byte[] data13 = new byte[2];
                data13[0] = flags[1];
                data13[1] = flags[2];
                return "Wheel Circumference Changed; New Wheel Circumference is " + BaseUtils.bytes2ToInt(data13,0);
            case 0x14:
                return "Spin Down Status;";
            case 0x15:
                byte[] data15 = new byte[2];
                data15[0] = flags[1];
                data15[1] = flags[2];
                return "Targeted Cadence Changed; New Targeted Cadence is "+  BaseUtils.bytes2ToInt(data15,0);
            case -1: // 0xff
                return "Control Permission Lost;";
            default:
                return "Reserved for Future Use";
        }

    }
}
