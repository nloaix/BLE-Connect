package com.jht.bleconnect.entity;

import android.util.Log;

public class TrainingStatus {
    private final String TAG = "TrainingStatus";
    private static TrainingStatus data;

    private byte[] status = new byte[1];
    public static TrainingStatus getInstance()
    {
        data = new TrainingStatus();
        return data;
    }
    private TrainingStatus(){}

    public void parseData(byte[] buffer)
    {
        Log.i(TAG, "parseData: buffer.length " + buffer.length);
        System.arraycopy(buffer, 1, status, 0, 1);
    }

    public String getTrainingStatus(){
        switch (status[0]){
            case 0x00:
                return "Other";
            case 0x01:
                return "Idle";
            case 0x02:
                return "Warming Up";
            case 0x03:
                return "Low Intensity Interval";
            case 0x04:
                return "High Intensity Interval";
            case 0x05:
                return "Recovery Interval";
            case 0x06:
                return "Isometric";
            case 0x07:
                return "Heart Rate Control";
            case 0x08:
                return "Fitness Test";
            case 0x09:
                return "Speed Outside of Control Region - Low (increase speed to return to controllable \n" +
                        "region)";
            case 0x0A:
                return "Speed Outside of Control Region - High (decrease speed to return to controllable \n" +
                        "region)";
            case 0x0B:
                return "Cool Down";
            case 0x0C:
                return "Watt Control";
            case 0x0D:
                return "Manual Mode (Quick Start)";
            case 0x0E:
                return "Pre-Workout";
            case 0x0F:
                return "Post-Workout";
            default:
                return "Reserved for Future Use";
        }
    }
}
