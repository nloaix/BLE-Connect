package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;


//2ACD
public class TreadmillData {
    private final String TAG = "TreadmillData";
    private static TreadmillData data;

    private byte[] flags = new byte[2];
    private byte[] instantaneous_speed;
    private byte[] average_speed;
    private byte[] total_distance;
    private byte[] inclination;
    private byte[] ramp_Angle_Setting;
    private byte[] positive_elevation_gain;
    private byte[] negative_elevation_gain;
    private byte[] instantaneous_pace;
    private byte[] average_pace;
    private byte[] total_energy;
    private byte[] energy_per_hour;
    private byte[] energy_per_minute;
    private byte[] heart_rate;
    private byte[] metabolic_equivalent;
    private byte[] elapsed_time;
    private byte[] remaining_time;
    private byte[] force_on_belt;
    private byte[] power_output;

    private boolean InsSpeed = false;
    private boolean avgSpeed = false;
    private boolean totalDistance = false;
    private boolean inclinationAndRampAngleSettingpresent = false;
    private boolean elevationGainpresent = false;
    private boolean instantaneousPace = false;
    private boolean averagePace = false;
    private boolean expendedEnergy = false;
    private boolean heartRate = false;
    private boolean metabolic = false;
    private boolean elapsedTime = false;
    private boolean remainingTime = false;
    private boolean forceOnBeltAndPowerOutput = false;

    private boolean flagChecked = false;
    private int expect_data_length = 0;

    public static TreadmillData getInstance()
    {
        data = new TreadmillData();
        return data;
    }
    private TreadmillData(){}

    public void parseData(byte[] buffer)
    {
        Log.i(TAG, "parseData: buffer.length " + buffer.length);
        System.arraycopy(buffer, 0, flags, 0, 2);
        checkEachFieldIfSupported();
        parseEachSupportedFeature(buffer);
    }
    private void checkEachFieldIfSupported()
    {
        if(flagChecked)
        {
            return;
        }

        byte firstbyte = (byte) (flags[0] ^ 0xFD); // 0xFA means all features are supported in 1st byte;
        byte secondbyte =(byte) (flags[1] ^ 0x1F); // 0x1F mean all features are supported in 2nd byte;

        expect_data_length = 2;

        if(isBitZero(firstbyte, 0))
        {
            //instantaneous speed is supported
            InsSpeed = true;
            instantaneous_speed = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "instantaneous speed is supported");
        }
        if(isBitZero(firstbyte, 1))
        {
            //average speed is supported
            avgSpeed = true;
            average_speed = new byte[2];
            expect_data_length += 2;
            Log.d(TAG, "average speed is supported");
        }

        if(isBitZero(firstbyte, 2))
        {
            //totalDistance is supported
            totalDistance = true;
            total_distance = new byte[3];
            expect_data_length += 3;
            Log.d(TAG,"totalDistance is supported");
        }
        if(isBitZero(firstbyte, 3))
        {
            //inclinationAndRampAngleSettingpresent is supported
            inclinationAndRampAngleSettingpresent = true;
            inclination = new byte[2];
            ramp_Angle_Setting = new byte[2];
            expect_data_length += 4;
            Log.d(TAG,"inclinationAndRampAngleSettingpresent is supported");
        }
        if(isBitZero(firstbyte, 4))
        {
            //elevation Gain present is supported
            elevationGainpresent = true;
            positive_elevation_gain = new byte[2];
            negative_elevation_gain = new byte[2];
            expect_data_length += 4;
            Log.d(TAG,"elevation Gain present is supported");
        }
        if(isBitZero(firstbyte, 5))
        {
            //instantaneousPace is supported
            instantaneousPace = true;
            instantaneous_pace = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"instantaneousPace is supported");
        }
        if(isBitZero(firstbyte, 6))
        {
            //averagePace is supported
            averagePace = true;
            average_pace = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"averagePace is supported");
        }
        if(isBitZero(firstbyte, 7))
        {
            // All Energy features are supported
            expendedEnergy = true;
            total_energy = new byte[2];
            energy_per_hour = new byte[2];
            energy_per_minute = new byte[1];
            expect_data_length += 5;
            Log.d(TAG,"expendedEnergy is supported");
        }
        if(isBitZero(secondbyte, 0))
        {
            // heartRate are supported
            heartRate = true;
            heart_rate = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"heartRate is supported");
        }
        if(isBitZero(secondbyte, 1))
        {
            //metabolic equivalent is supported
            metabolic = true;
            metabolic_equivalent = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"metabolic is supported");
        }
        if(isBitZero(secondbyte, 2))
        {
            //elapsed time is supported
            elapsedTime = true;
            elapsed_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"elapsed time is supported");
        }
        if(isBitZero(secondbyte, 3))
        {
            //remaining time is supported
            remainingTime = true;
            remaining_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"remaining time is supported");
        }
        if(isBitZero(secondbyte, 4))
        {
            //Force on Belt is supported
            forceOnBeltAndPowerOutput = true;
            force_on_belt = new byte[2];
            power_output = new byte[2];
            expect_data_length += 4;
            Log.d(TAG,"Force on Belt is supported");
        }
        flagChecked = true;
    }

    /**
     *
     * @param value, the xor result, 0 means feature is supported. 1 means feature is not supported
     * @param bit, the bit of value to check
     * @return
     */
    private boolean isBitZero(byte value, int bit)
    {
        int power2 = (int) Math.pow(2, bit);
        int tmp = value & power2;
        if(tmp == 0)
        {
            return true;
        }else{
            return false;
        }
    }

    private void parseEachSupportedFeature(byte[] buffer)
    {
        if(buffer.length != expect_data_length)
        {
            Log.d(TAG, "buffer length is "+buffer.length);
            Log.d(TAG, "expect data length is "+expect_data_length);
            return;
        }

        int startIndex = 2;
        if(InsSpeed) {
            System.arraycopy(buffer, startIndex, instantaneous_speed, 0, 2);
            startIndex += 2;
        }
        if(avgSpeed) {
            System.arraycopy(buffer, startIndex, average_speed, 0, 2);
            startIndex += 2;
        }

        if(totalDistance) {
            System.arraycopy(buffer, startIndex, total_distance, 0, 3);
            startIndex += 3;
        }

        if(inclinationAndRampAngleSettingpresent) {
            System.arraycopy(buffer, startIndex, inclination, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, ramp_Angle_Setting, 0, 2);
            startIndex += 2;
        }

        if(elevationGainpresent) {
            System.arraycopy(buffer, startIndex, positive_elevation_gain, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, negative_elevation_gain, 0, 2);
            startIndex += 2;
        }

        if(instantaneousPace) {
            System.arraycopy(buffer, startIndex, instantaneous_pace, 0, 1);
            startIndex += 1;
        }

        if(averagePace) {
            System.arraycopy(buffer, startIndex, average_pace, 0, 1);
            startIndex += 1;
        }

        if(expendedEnergy) {
            System.arraycopy(buffer, startIndex, total_energy, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, energy_per_hour, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, energy_per_minute, 0, 1);
            startIndex += 1;
        }

        if(heartRate) {
            System.arraycopy(buffer, startIndex, heart_rate, 0, 1);
            startIndex += 1;
        }

        if(metabolic) {
            System.arraycopy(buffer, startIndex, metabolic_equivalent, 0, 1);
            startIndex += 1;
        }
        if(elapsedTime) {
            System.arraycopy(buffer, startIndex, elapsed_time, 0, 2);
            startIndex += 2;
        }
        if(remainingTime) {
            System.arraycopy(buffer, startIndex, remaining_time, 0, 2);
            startIndex += 2;
        }

        if(forceOnBeltAndPowerOutput) {
            System.arraycopy(buffer, startIndex, force_on_belt, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, power_output, 0, 2);
        }

    }

    public double getInstantaneousSpeed()
    {
        if(InsSpeed) {
            return BaseUtils.bytes2ToInt(instantaneous_speed, 0) * 0.01;
        }else{
            return 0;
        }
    }

    public double getAverageSpeed()
    {
        if(avgSpeed)
        {
            return BaseUtils.bytes2ToInt(average_speed, 0) * 0.01;
        }else{
            return 0;
        }
    }

    public int getTotalDistance()
    {
        if(totalDistance)
        {
            return BaseUtils.byte3ToInt(total_distance, 0);
        }else{
            return 0;
        }
    }

    public double getinclination()
    {
        if(inclinationAndRampAngleSettingpresent)
        {
            return BaseUtils.bytes2ToInt(inclination, 0) * 0.1;
        }else{
            return 0 * 0.0;
        }
    }

    public double getRamp_Angle_Setting()
    {
        if(inclinationAndRampAngleSettingpresent)
        {
            return BaseUtils.bytes2ToInt(ramp_Angle_Setting, 0) * 0.1;
        }else{
            return 0;
        }
    }


    public double getPositive_elevation_gain()
    {
        if(elevationGainpresent)
        {
            return BaseUtils.bytes2ToInt(positive_elevation_gain, 0) * 0.1;
        }else{
            return 0;
        }
    }

    public double getNegative_elevation_gain()
    {
        if(elevationGainpresent)
        {
            return BaseUtils.bytes2ToInt(negative_elevation_gain, 0) * 0.1;
        }else{
            return 0;
        }
    }

    public double getInstantaneousPace()
    {
        if(instantaneousPace)
        {
            return BaseUtils.byte1ToInt(instantaneous_pace[0]) * 0.1;
        }else{
            return 0;
        }
    }

    public double getAverage_pace()
    {
        if(averagePace)
        {
            return BaseUtils.byte1ToInt(average_pace[0]) * 0.1;
        }else{
            return 0;
        }
    }

    public int getTotalEnergy()
    {
        if(expendedEnergy)
        {
            return BaseUtils.bytes2ToInt(total_energy, 0);
        }else{
            return 0;
        }
    }

    public int getEnergyPerHour()
    {
        if(expendedEnergy)
        {
            return BaseUtils.bytes2ToInt(energy_per_hour, 0);
        }else{
            return 0;
        }
    }

    public int getEnergyPerMinute()
    {
        if(expendedEnergy)
        {
            return BaseUtils.byte1ToInt(energy_per_minute[0]);
        }else{
            return 0;
        }
    }

    public int getHeartRate()
    {
        if(heartRate)
        {
            return BaseUtils.byte1ToInt(heart_rate[0]);
        }else{
            return 0;
        }
    }

    public double getMetabolicEquivalent()
    {
        if(metabolic)
        {
            return BaseUtils.byte1ToInt(metabolic_equivalent[0]) * 0.1;
        }else{
            return 0;
        }
    }

    public int getElapedTime()
    {
        if(elapsedTime)
        {
            return BaseUtils.bytes2ToInt(elapsed_time, 0);
        }else{
            return 0;
        }
    }

    public int getRemainingTime()
    {
        if(remainingTime)
        {
            return BaseUtils.bytes2ToInt(remaining_time, 0);
        }else{
            return 0;
        }
    }

    public int getforce_on_belt()
    {
        if(forceOnBeltAndPowerOutput)
        {
            return BaseUtils.bytes2ToInt(force_on_belt, 0);
        }else{
            return 0;
        }
    }

    public int getPower_output()
    {
        if(forceOnBeltAndPowerOutput)
        {
            return BaseUtils.bytes2ToInt(power_output, 0);
        }else{
            return 0;
        }
    }

}
