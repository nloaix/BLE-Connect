package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;

public class StairClimberData {
    private final String TAG = "StairClimberData";
    private static StairClimberData data;
    private byte[] flags = new byte[2];

    private byte[] floors;
    private byte[] step_per_minute;
    private byte[] average_Step_Rate;
    private byte[] positive_elevation_gain;
    private byte[] stride_count;
    private byte[] total_energy;
    private byte[] energy_per_hour;
    private byte[] energy_per_minute;
    private byte[] heart_rate;
    private byte[] metabolic_equivalent;
    private byte[] elapsed_time;
    private byte[] remaining_time;

    private boolean floorsField = false;
    private boolean stepPerMinute = false;
    private boolean averageStepRate = false;
    private boolean positiveElevationGain = false;
    private boolean strideCount = false;
    private boolean energy = false;
    private boolean heartRate = false;
    private boolean metabolic = false;
    private boolean elapsedTime = false;
    private boolean remainingTime = false;

    private boolean flagChecked = false;
    private int expect_data_length = 0;

    public static StairClimberData getInstance() {
        data = new StairClimberData();
        return data;
    }

    private StairClimberData() {
    }

    public void parseData(byte[] buffer) {
        Log.i(TAG, "parseData: buffer.length " + buffer.length);
        System.arraycopy(buffer, 0, flags, 0, 2);
        checkEachFieldIfSupported();
        parseEachSupportedFeature(buffer);

    }

    private void checkEachFieldIfSupported() {
        if (flagChecked) {
            return;
        }
        byte firstbyte = (byte) (flags[0] ^ 0xFE); // 0xFE means all features are supported in 1st byte;
        byte secondbyte = (byte) (flags[1] ^ 0xFF); // 0x1F mean all features are supported in 2nd byte;

        expect_data_length = 2;

        if (isBitZero(firstbyte, 0)) {
            //floorsField is supported
            floorsField = true;
            floors = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "floorsField is supported");
        }
        if (isBitZero(firstbyte, 1)) {
            //stepPerMinute is supported
            stepPerMinute = true;
            step_per_minute = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "Step per Minute is supported");
        }

        if (isBitZero(firstbyte, 2)) {
            //averageStepRate is supported
            averageStepRate = true;
            average_Step_Rate = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "averageStepRate is supported");
        }
        if (isBitZero(firstbyte, 3)) {
            //Positive Elevation Gain is supported
            positiveElevationGain = true;
            positive_elevation_gain = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "Positive Elevation Gain is supported");
        }

        if (isBitZero(firstbyte, 4)) {
            //strideCount is supported
            strideCount = true;
            stride_count = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "strideCount is supported");
        }
        if (isBitZero(firstbyte, 5)) {
            //Expended Energy is supported
            energy = true;
            total_energy = new byte[2];
            energy_per_hour = new byte[2];
            energy_per_minute = new byte[1];
            expect_data_length += 5;
            Log.d(TAG, "Expended Energy is supported");
        }
        if (isBitZero(firstbyte, 6)) {
            // heartRate are supported
            heartRate = true;
            heart_rate = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"heartRate is supported");
        }

        if (isBitZero(firstbyte, 7)) {
            //metabolic is supported
            metabolic = true;
            metabolic_equivalent = new byte[1];
            expect_data_length += 1;
            Log.d(TAG, "metabolic is supported");
        }

        if (isBitZero(secondbyte, 0)) {
            //elapsed time is supported
            elapsedTime = true;
            elapsed_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "elapsed time is supported");
        }
        if (isBitZero(secondbyte, 1)) {
            //remaining time is supported
            remainingTime = true;
            remaining_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "remaining time is supported");
        }

        flagChecked = true;
    }


    /**
     * @param value, the xor result, 0 means feature is supported. 1 means feature is not supported
     * @param bit,   the bit of value to check
     * @return
     */
    private boolean isBitZero(byte value, int bit) {
        int power2 = (int) Math.pow(2, bit);
        int tmp = value & power2;
        if (tmp == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void parseEachSupportedFeature(byte[] buffer) {
        if (buffer.length != expect_data_length) {
            Log.d(TAG, "buffer length is " + buffer.length);
            Log.d(TAG, "expect data length is " + expect_data_length);
            return;
        }

        int startIndex = 2;
        if (floorsField) {
            System.arraycopy(buffer, startIndex, floors, 0, 2);
            startIndex += 2;
        }
        if (stepPerMinute) {
            System.arraycopy(buffer, startIndex, step_per_minute, 0, 2);
            startIndex += 2;
        }
        if (averageStepRate) {
            System.arraycopy(buffer, startIndex, average_Step_Rate, 0, 2);
            startIndex += 2;
        }
        if (positiveElevationGain) {
            System.arraycopy(buffer, startIndex, positive_elevation_gain, 0, 2);
            startIndex += 2;
        }
        if(strideCount){
            System.arraycopy(buffer, startIndex, stride_count, 0, 2);
            startIndex += 2;
        }
        if (energy) {
            System.arraycopy(buffer, startIndex, total_energy, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, energy_per_hour, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, energy_per_minute, 0, 1);
            startIndex += 1;
        }
        if (heartRate) {
            System.arraycopy(buffer, startIndex, heart_rate, 0, 1);
            startIndex += 1;
        }
        if (metabolic) {
            System.arraycopy(buffer, startIndex, metabolic_equivalent, 0, 1);
            startIndex += 1;
        }
        if (elapsedTime) {
            System.arraycopy(buffer, startIndex, elapsed_time, 0, 2);
            startIndex += 2;
        }
        if (remainingTime) {
            System.arraycopy(buffer, startIndex, remaining_time, 0, 2);
        }
    }

    public int getFloors() {
        if (floorsField) {
            return BaseUtils.bytes2ToInt(floors, 0);
        } else {
            return 0;
        }
    }

    public int getStepPerMinute() {
        if (stepPerMinute) {
            return BaseUtils.bytes2ToInt(step_per_minute, 0);
        } else {
            return 0;
        }
    }

    public int getAverageStepRate() {

        if (averageStepRate) {
            return BaseUtils.bytes2ToInt(average_Step_Rate, 0);
        } else {
            return 0;
        }
    }

    public int getPositiveElevationGain() {

        if (positiveElevationGain) {
            return BaseUtils.bytes2ToInt(positive_elevation_gain, 0);
        } else {
            return 0;
        }
    }
    public int getStrideCount(){
        if (strideCount) {
            return BaseUtils.bytes2ToInt(stride_count, 0);
        } else {
            return 0;
        }
    }

    public int getTotalEnergy() {
        if (energy) {
            return BaseUtils.bytes2ToInt(total_energy, 0);
        } else {
            return 0;
        }
    }
    public int getEnergyPerHour() {
        if (energy) {
            return BaseUtils.bytes2ToInt(energy_per_hour, 0);
        } else {
            return 0;
        }
    }

    public int getEnergyPerMinute() {
        if (energy) {
            return BaseUtils.byte1ToInt(energy_per_minute[0]);
        } else {
            return 0;
        }
    }

    public int getHeartRate() {
        if (heartRate) {
            return BaseUtils.byte1ToInt(heart_rate[0]);
        } else {
            return 0;
        }
    }

    public int getMetabolicEquivalent() {
        if (metabolic) {
            return BaseUtils.byte1ToInt(metabolic_equivalent[0]);
        } else {
            return 0;
        }
    }

    public int getElapedTime() {
        if (elapsedTime) {
            return BaseUtils.bytes2ToInt(elapsed_time, 0);
        } else {
            return 0;
        }
    }

    public int getRemainingTime() {
        if (remainingTime) {
            return BaseUtils.bytes2ToInt(remaining_time, 0);
        } else {
            return 0;
        }
    }

    public String convert2String() {
        return    "Floors is <font color='red'>" + getFloors()
                + "</font> ;<br/> StrideCount is <font color='red'>" + getStrideCount()
                + "</font> ;<br/> PositiveElevationGain is <font color='red'>" + getPositiveElevationGain()
                + "</font> m ;<br/> TotalEnergy is <font color='red'>" + getTotalEnergy()
                + "</font> Calorie ;<br/> HeartRate is <font color='red'>" + getHeartRate()
                + "</font> bpm ;<br/> elapsed time is <font color='red'>" + getElapedTime()
                + "</font> second;<br/> StepPerMinute is <font color='red'>" + getStepPerMinute()
                + "</font> step_per_minute ;<br/> AverageStepRate is <font color='red'>" + getAverageStepRate()
                + "</font> step_per_minute;<br/> RemainingTime is <font color='red'>" + getRemainingTime()
                + "</font> second;<br/>"
                ;
    }

}
