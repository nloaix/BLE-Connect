package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;

public class RowerData {

    private final String TAG = "RowerData";
    private static RowerData data;
    private byte[] flags = new byte[2];

    private byte[] strokeRate;
    private byte[] strokeCount;
    private byte[] average_stroke_rate;
    private byte[] total_distance;
    private byte[] instantaneous_pace;
    private byte[] average_pace;
    private byte[] instantaneous_power;
    private byte[] average_power;
    private byte[] resistance_level;
    private byte[] total_energy;
    private byte[] energy_per_hour;
    private byte[] energy_per_minute;
    private byte[] heart_rate;
    private byte[] metabolic_equivalent;
    private byte[] elapsed_time;
    private byte[] remaining_time;

    private boolean StrokeRateAndStrokeCount = false;
    private boolean averageStroke = false;
    private boolean totalDistance = false;
    private boolean insPace = true;
    private boolean  averagePace = true;
    private boolean insPower = true;
    private boolean averagePower = true;
    private boolean resistanceLevel = true;
    private boolean energy = false;
    private boolean heartRate = false;
    private boolean metabolic = false;
    private boolean elapsedTime = false;
    private boolean remainingTime = false;

    private boolean flagChecked = false;
    private int expect_data_length = 0;

    public static RowerData getInstance() {
        data = new RowerData();
        return data;
    }

    private RowerData() {
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
            //Stroke Rate And Stroke Count is supported
            StrokeRateAndStrokeCount = true;
            strokeRate =  new byte[1];
            strokeCount = new byte[2];
            expect_data_length += 3;
            Log.d(TAG, "Stroke Rate And Stroke Count is supported");
        }
        if (isBitZero(firstbyte, 1)) {
            //averageStroke is supported
            averageStroke = true;
            average_stroke_rate = new byte[1];
            expect_data_length += 1;
            Log.d(TAG, "averageStroke is supported");
        }

        if (isBitZero(firstbyte, 2)) {
            //totalDistance is supported
            totalDistance = true;
            total_distance = new byte[3];
            expect_data_length += 3;
            Log.d(TAG, "totalDistance is supported");
        }
        if (isBitZero(firstbyte, 3)) {
            //Instantaneous Pace is supported
            insPace = true;
            instantaneous_pace = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "Instantaneous Pace is supported");
        }
        if (isBitZero(firstbyte, 4)) {
            //averagePace is supported
            averagePace = true;
            average_pace = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "averagePace is supported");
        }
        if (isBitZero(firstbyte, 5)) {
            //Instantaneous Power is supported
            insPower = true;
            instantaneous_power = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "Instantaneous Power is supported");
        }
        if (isBitZero(firstbyte, 6)) {
            //Average Poweris supported
            averagePower = true;
            average_power = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "Average Power is supported");
        }
        if (isBitZero(firstbyte, 7)) {
            //  Resistance Level supported
            resistanceLevel = true;
            resistance_level = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "  Resistance Level is supported");
        }
        if (isBitZero(secondbyte, 0)) {
            //Expended Energy is supported
            energy = true;
            total_energy = new byte[2];
            energy_per_hour = new byte[2];
            energy_per_minute = new byte[1];
            expect_data_length += 5;
            Log.d(TAG, "Expended Energy is supported");
        }
        if (isBitZero(secondbyte, 1)) {
            // heartRate are supported
            heartRate = true;
            heart_rate = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"heartRate is supported");
        }

        if (isBitZero(firstbyte, 2)) {
            //metabolic is supported
            metabolic = true;
            metabolic_equivalent = new byte[1];
            expect_data_length += 1;
            Log.d(TAG, "metabolic is supported");
        }

        if (isBitZero(secondbyte, 3)) {
            //elapsed time is supported
            elapsedTime = true;
            elapsed_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "elapsed time is supported");
        }
        if (isBitZero(secondbyte, 4)) {
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
        if (StrokeRateAndStrokeCount) {
            System.arraycopy(buffer, startIndex, strokeRate, 0, 1);
            startIndex += 1;
            System.arraycopy(buffer, startIndex, strokeCount, 0, 2);
            startIndex += 2;
        }
        if (averageStroke) {
            System.arraycopy(buffer, startIndex, average_stroke_rate, 0, 1);
            startIndex += 1;
        }
        if (totalDistance) {
            System.arraycopy(buffer, startIndex, total_distance, 0, 3);
            startIndex += 3;
        }
        if (insPace) {
            System.arraycopy(buffer, startIndex, instantaneous_pace, 0, 2);
            startIndex += 2;
        }
        if(averagePace){
            System.arraycopy(buffer, startIndex, average_pace, 0, 2);
            startIndex += 2;
        }
        if(insPower){
            System.arraycopy(buffer, startIndex, instantaneous_power, 0, 2);
            startIndex += 2;
        }
        if(averagePower){
            System.arraycopy(buffer, startIndex, average_power, 0, 2);
            startIndex += 2;
        }
        if(resistanceLevel){
            System.arraycopy(buffer, startIndex, resistance_level, 0, 2);
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

    public int getStrokeRate() {
        if (StrokeRateAndStrokeCount) {
            return BaseUtils.byte1ToInt(strokeRate[0]);
        } else {
            return 0;
        }
    }

    public int getStrokeCount() {
        if (StrokeRateAndStrokeCount) {
            return BaseUtils.bytes2ToInt(strokeCount,0);
        } else {
            return 0;
        }
    }

    public int getAverageStroke() {
        if (averageStroke) {
            return BaseUtils.byte1ToInt(average_stroke_rate[0]);
        } else {
            return 0;
        }
    }

    public int getTotalDistance() {
        if (totalDistance) {
            return BaseUtils.byte3ToInt(total_distance, 0);
        } else {
            return 0;
        }
    }
    public int getInsPace() {
        if (insPace) {
            return BaseUtils.bytes2ToInt(instantaneous_pace, 0);
        } else {
            return 0;
        }
    }

    public int getAveragePace() {
        if (averagePace) {
            return BaseUtils.bytes2ToInt(average_pace, 0);
        } else {
            return 0;
        }
    }
    public int getInsPower() {
        if (insPower) {
            return BaseUtils.bytes2ToInt(instantaneous_power, 0);
        } else {
            return 0;
        }
    }

    public int getAveragePower() {
        if (averagePower) {
            return BaseUtils.bytes2ToInt(average_power, 0);
        } else {
            return 0;
        }
    }

    public int getResistanceLevel() {
        if (resistanceLevel) {
            return BaseUtils.bytes2ToInt(resistance_level, 0);
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
        return    "Stroke Rate is <font color='red'>" + getStrokeRate()
                + "</font> stroke_per_minute;<br/> Stroke Count is <font color='red'>" + getStrokeCount()
                + "</font> ;<br/> average stroke rate is <font color='red'>" + getAverageStroke()
                + "</font> stroke_per_minute ;<br/> TotalEnergy is <font color='red'>" + getTotalEnergy()
                + "</font> Calorie ;<br/> HeartRate is <font color='red'>" + getHeartRate()
                + "</font> bpm ;<br/> elapsed time is <font color='red'>" + getElapedTime()
                + "</font> second;<br/> Resistance Level is <font color='red'>" + getResistanceLevel()
                + "</font> ;<br/> TotalDistance is <font color='red'>" + getTotalDistance()
                + "</font> metre;<br/> RemainingTime is <font color='red'>" + getRemainingTime()
                + "</font> second;<br/> AveragePower is <font color='red'>" + getAveragePower()
                + "</font> watt;<br/> InsPower is <font color='red'>" + getInsPower()
                + "</font> watt;<br/> InsPace is <font color='red'>" + getInsPace()
                + "</font> second;<br/> AveragePace is <font color='red'>" + getAveragePace()
                + "</font> second;<br/> EnergyPerHour is <font color='red'>" + getEnergyPerHour()
                + "</font> kilogram_calorie;<br/> EnergyPerMinute is <font color='red'> " + getEnergyPerMinute()
                + "</font> kilogram_calorie;<br/>"
                ;
    }
}
