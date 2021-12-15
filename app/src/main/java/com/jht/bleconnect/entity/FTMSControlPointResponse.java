package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;

import java.util.Set;

public class FTMSControlPointResponse {
    private final String TAG = "FTMSControlPointResponse";
    private static FTMSControlPointResponse data;
    private byte[] all_data;

    public static FTMSControlPointResponse getInstance(byte[] buffer)
    {
        data = new FTMSControlPointResponse();
        data.parseData(buffer);
        return data;
    }

    private FTMSControlPointResponse(){}

    public void parseData(byte[] buffer)
    {
        all_data = new byte[buffer.length];
        System.arraycopy(buffer, 0, all_data, 0, buffer.length);
    }

    public String getResponseCode_OpCode(){
        Log.i(TAG, "getResponseCode_OpCode: " + Integer.toHexString(BaseUtils.byte1ToInt(all_data[0])));
        String unsignedString = Integer.toUnsignedString(BaseUtils.byte1ToInt(all_data[0]), 16);
        return "0x" + unsignedString;
    }

    public String getRequestOpCode(){
        Log.i(TAG, "getRequestOpCode: " + all_data[1]);
        switch(BaseUtils.byte1ToInt(all_data[1])){
            case 0x00:
                return "request code: 0x00 ; Request Control";
            case 0x01:
                return "request code: 0x01 ; Reset";
            case 0x02:
                return "request code: 0x02 ; Set Target Speed";
            case 0x03:
                return "request code: 0x03; Set Target Inclination";
            case 0x04:
                return "request code: 0x04; Set Target Resistance Level";
            case 0x05:
                return "request code: 0x05; Set Target Power";
            case 0x06:
                return "request code: 0x06; Set Target Heart Rate";
            case 0x07:
                return "request code: 0x07; Start or Resume";
            case 0x08:
                return "request code: 0x08; Stop or Pause";
            case 0x09:
                return "request code: 0x09; Set Targeted Expended Energy";
            case 0x0A:
                return "request code: 0x0A; Set Targeted Number of Steps";
            case 0x0B:
                return "request code: 0x0B; Set Targeted Number of Strides";
            case 0x0C:
                return "request code: 0x0C; Set Targeted Distance";
            case 0x0D:
                return "request code: 0x0D; Set Targeted Training Time";
            case 0x0E:
                return "\n request code: 0x0E; Set Targeted Time in Two Heart Rate Zones";
            case 0x0F:
                return "\n request code: 0x0F; Set Targeted Time in Three Heart Rate Zones";
            case 0x10:
                return "\n request code: 0x10; Set Targeted Time in Five Heart Rate Zones";
            case 0x11:
                return "\n request code: 0x11; Set Indoor Bike Simulation Parameters";
            case 0x12:
                return "request code: 0x12 ; Set Wheel Circumference";
            case 0x13:
                return "request code: 0x13 ; Spin Down Control";
            case 0x14:
                return "request code: 0x14 ; Set Targeted Cadence";
            case 0x80:
                return "request code: 0x80 ;Response Code";
            default:
                return "Reserved for Future Use";
        }
    }

    public String getResultCode(){
        Log.i(TAG, "getResultCode: " + all_data[2]);
        switch(BaseUtils.byte1ToInt(all_data[2])){
            case 0x01:
                return "success";
            case 0x02:
                return "Op Code not supported";
            case 0x03:
                return "Invalid Parameter";
            case 0x04:
                return "Operation Failed";
            case 0x05:
                return "Control Not Permitted";
            default:
                return "Reserved for Future Use";
        }
    }



}
