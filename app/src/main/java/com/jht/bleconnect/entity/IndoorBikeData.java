package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;


public class IndoorBikeData {
    private final String TAG = "IndoorBikeData";
    private static IndoorBikeData data;
    private byte[] flags = new byte[2];
    private byte[] instantaneous_speed;
    private byte[] average_speed;
    private byte[] instantaneous_cadence;
    private byte[] average_cadence;
    private byte[] total_distance;
    private byte[] resistance_level;
    private byte[] instantaneous_power;
    private byte[] average_power;
    private byte[] total_energy;
    private byte[] energy_per_hour;
    private byte[] energy_per_minute;
    private byte[] heart_rate;
    private byte[] metabolic_equivalent;
    private byte[] elapsed_time;
    private byte[] remaining_time;

    private boolean InsSpeed = false;
    private boolean avgSpeed = false;
    private boolean insCadence = false;
    private boolean avgCadence = false;
    private boolean totalDistance = false;
    private boolean resistanceLevel = false;
    private boolean insPower = false;
    private boolean avgPower = false;
    private boolean energy = false;
    private boolean heartRate = false;
    private boolean metabolic = false;
    private boolean elapsedTime = false;
    private boolean remainingTime = false;

    private boolean flagChecked = false;
    private int expect_data_length = 0;

    public static IndoorBikeData getInstance()
    {
        data = new IndoorBikeData();
        return data;
    }
    private IndoorBikeData(){}

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

        byte firstbyte = (byte) (flags[0] ^ 0xFA); // 0xFA means all features are supported in 1st byte;
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
            //instantaneous cadence is supported
            insCadence = true;
            instantaneous_cadence = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"instantaneous cadence is supported");
        }
        if(isBitZero(firstbyte, 3))
        {
            //average cadence is supported
            avgCadence = true;
            average_cadence = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"average cadence is supported");
        }
        if(isBitZero(firstbyte, 4))
        {
            //Total distance is supported
            totalDistance = true;
            total_distance = new byte[3];
            expect_data_length += 3;
            Log.d(TAG,"Total distance is supported");
        }
        if(isBitZero(firstbyte, 5))
        {
            //resistance level is supported
            resistanceLevel = true;
            resistance_level = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"resistance level is supported");
        }
        if(isBitZero(firstbyte, 6))
        {
            //Instantaneous power is supported
            insPower = true;
            instantaneous_power = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"Instantaneous power is supported");
        }
        if(isBitZero(firstbyte, 7))
        {
            //average power is supported
            avgPower = true;
            average_power = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"average power is supported");
        }
        if(isBitZero(secondbyte, 0))
        {
            // All Energy features are supported
            energy = true;
            total_energy = new byte[2];
            energy_per_hour = new byte[2];
            energy_per_minute = new byte[1];
            expect_data_length += 5;
            Log.d(TAG,"energy is supported");
        }
        if(isBitZero(secondbyte, 1))
        {
            //heart rate is supported
            heartRate = true;
            heart_rate = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"heart rate is supported");
        }
        if(isBitZero(secondbyte, 2))
        {
            //metabolic equivalent is supported
            metabolic = true;
            metabolic_equivalent = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"metabolic is supported");
        }
        if(isBitZero(secondbyte, 3))
        {
            //elapsed time is supported
            elapsedTime = true;
            elapsed_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"elapsed time is supported");
        }
        if(isBitZero(secondbyte, 4))
        {
            //remaining time is supported
            remainingTime = true;
            remaining_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"remaining time is supported");
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
        if(insCadence) {
            System.arraycopy(buffer, startIndex, instantaneous_cadence, 0, 2);
            startIndex += 2;
        }
        if(avgCadence) {
            System.arraycopy(buffer, startIndex, average_cadence, 0, 2);
            startIndex += 2;
        }
        if(totalDistance) {
            System.arraycopy(buffer, startIndex, total_distance, 0, 3);
            startIndex += 3;
        }
        if(resistanceLevel) {
            System.arraycopy(buffer, startIndex, resistance_level, 0, 2);
            startIndex += 2;
        }
        if(insPower) {
            System.arraycopy(buffer, startIndex, instantaneous_power, 0, 2);
            startIndex += 2;
        }
        if(avgPower) {
            System.arraycopy(buffer, startIndex, average_power, 0, 2);
            startIndex += 2;
        }
        if(energy) {
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

    public double getInstantaneousCadence()
    {
        if(insCadence)
        {
            return BaseUtils.bytes2ToInt(instantaneous_cadence, 0);
        }else{
            return 0;
        }
    }

    public double getAverageCadence()
    {
        if(avgCadence)
        {
            return BaseUtils.bytes2ToInt(average_cadence, 0);
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

    public int getResistanceLevel()
    {
        if(resistanceLevel)
        {
            return BaseUtils.bytes2ToInt(resistance_level, 0);
        }else{
            return 0;
        }
    }

    public int getInstantaneousPower()
    {
        if(insPower)
        {
            return BaseUtils.bytes2ToInt(instantaneous_power, 0);
        }else{
            return 0;
        }
    }

    public int getAveragePower()
    {
        if(avgPower)
        {
            return BaseUtils.bytes2ToInt(average_power, 0);
        }else{
            return 0;
        }
    }

    public int getTotalEnergy()
    {
        if(energy)
        {
            return BaseUtils.bytes2ToInt(total_energy, 0);
        }else{
            return 0;
        }
    }

    public int getEnergyPerHour()
    {
        if(energy)
        {
            return BaseUtils.bytes2ToInt(energy_per_hour, 0);
        }else{
            return 0;
        }
    }

    public int getEnergyPerMinute()
    {
        if(energy)
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

}

