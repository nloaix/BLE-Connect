package com.jht.bleconnect.entity;

import android.util.Log;

import com.jht.bleconnect.common.BaseUtils;

public class CrossTrainerData {
    private final String TAG = "CrossTrainerData";
    private static CrossTrainerData data;
    private byte[] flags = new byte[3];

    private byte[] instantaneous_speed;
    private byte[] average_speed;
    private byte[] total_distance;
    private byte[] step_per_minute;
    private byte[] average_step_rate;
    private byte[] stride_count;
    private byte[] positive_elevation_gain;
    private byte[] negative_elevation_gain;
    private byte[] inclination;
    private byte[] ramp_angle_setting;
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
    private boolean totalDistance = false;
    private boolean StepCountPresent = false;
    private boolean strideCount = false;
    private boolean elevationGain = false;
    private boolean inclinationAndRampAngleSetting = false;
    private boolean resistanceLevel = false;
    private boolean insPower = false;
    private boolean avgPower = false;
    private boolean energy = false;
    private boolean heartRate = false;
    private boolean metabolic = false;
    private boolean elapsedTime = false;
    private boolean remainingTime = false;

    private boolean movementDirection = false; //Forward or Backward

    private boolean flagChecked = false;
    private int expect_data_length = 0;

    public static CrossTrainerData getInstance()
    {
        data = new CrossTrainerData();
        return data;
    }

    private CrossTrainerData(){}

    public void parseData(byte[] buffer)
    {
        System.arraycopy(buffer, 0, flags, 0, 3);
        checkEachFieldIfSupported();
        parseEachSupportedFeature(buffer);
    }

    private void checkEachFieldIfSupported()
    {
        if(flagChecked)
        {
            return;
        }
        byte firstbyte = (byte) (flags[0] ^ 0xFD); // 0xFD means all features are supported in 1st byte;
        byte secondbyte = (byte) (flags[1] ^ 0xFF); // 0xFF mean all features are supported in 2nd byte;
        byte threebyte = (byte) (flags[2] ^ 0xFF);

        expect_data_length = 3;

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
            //Step Count present is supported
            StepCountPresent = true;
            step_per_minute = new byte[2];
            average_step_rate = new byte[2];
            expect_data_length += 4;
            Log.d(TAG,"Step Count present is supported");
        }
        if(isBitZero(firstbyte, 4))
        {
            //Stride Count present is supported
            strideCount = true;
            stride_count = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"Stride Count present is supported");
        }
        if(isBitZero(firstbyte, 5))
        {
            //elevationGain is supported
            elevationGain = true;
            positive_elevation_gain = new byte[2];
            negative_elevation_gain = new byte[2];
            expect_data_length += 4;
            Log.d(TAG,"elevationGain is supported");
        }
        if(isBitZero(firstbyte, 6))
        {
            //inclinationAndRampAngleSetting is supported
            inclinationAndRampAngleSetting = true;
            inclination = new byte[2];
            ramp_angle_setting = new byte[2];
            expect_data_length += 4;
            Log.d(TAG,"inclinationAndRampAngleSetting power is supported");
        }
        if(isBitZero(firstbyte, 7))
        {
            //resistance level is supported
            resistanceLevel = true;
            resistance_level = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"resistance level is supported");
        }
        if(isBitZero(secondbyte, 0))
        {
            // insPower features are supported
            insPower = true;
            instantaneous_power = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"insPower is supported");
        }
        if(isBitZero(secondbyte, 1))
        {
            //avgPower is supported
            avgPower = true;
            average_power = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"avgPower is supported");
        }
        if(isBitZero(secondbyte, 2))
        {
            //energy is supported
            energy = true;
            total_energy = new byte[2];
            energy_per_hour = new byte[2];
            energy_per_minute = new byte[1];
            expect_data_length += 5;
            Log.d(TAG,"energy is supported");
        }
        if(isBitZero(secondbyte, 3))
        {
            // heartRate are supported
            heartRate = true;
            heart_rate = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"heartRate is supported");
        }
        if(isBitZero(secondbyte, 4))
        {
            //metabolic is supported
            metabolic = true;
            metabolic_equivalent = new byte[1];
            expect_data_length += 1;
            Log.d(TAG,"metabolic is supported");
        }


        if(isBitZero(secondbyte, 5))
        {
            //elapsed time is supported
            elapsedTime = true;
            elapsed_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"elapsed time is supported");
        }
        if(isBitZero(secondbyte, 6))
        {
            //remaining time is supported
            remainingTime = true;
            remaining_time = new byte[2];
            expect_data_length += 2;
            Log.d(TAG,"remaining time is supported");
        }
        if(isBitZero(secondbyte, 7))
        {
            //movementDirection is supported
            movementDirection = true;
            Log.d(TAG,"movementDirection time is Backward ");
        }else {
            Log.d(TAG,"movementDirection time is Forward ");
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

        int startIndex = 3;
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
        if(StepCountPresent) {
            System.arraycopy(buffer, startIndex, step_per_minute, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, average_step_rate, 0, 2);
            startIndex += 2;
        }
        if(strideCount) {
            System.arraycopy(buffer, startIndex, stride_count, 0, 2);
            startIndex += 2;
        }

        if(elevationGain) {
            System.arraycopy(buffer, startIndex, positive_elevation_gain, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, negative_elevation_gain, 0, 2);
            startIndex += 2;
        }

        if(inclinationAndRampAngleSetting) {
            System.arraycopy(buffer, startIndex, inclination, 0, 2);
            startIndex += 2;
            System.arraycopy(buffer, startIndex, ramp_angle_setting, 0, 2);
            startIndex += 2;
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

    public int getInstantaneousSpeed()
    {
        if(InsSpeed) {
            return BaseUtils.bytes2ToInt(instantaneous_speed, 0);
        }else{
            return 0;
        }
    }

    public int getAverageSpeed()
    {
        if(avgSpeed)
        {
            return BaseUtils.bytes2ToInt(average_speed, 0);
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
    public int getStepPerMinute(){

        if(StepCountPresent)
        {
            return BaseUtils.bytes2ToInt(step_per_minute, 0);
        }else{
            return 0;
        }
    }

    public int getAverageStepRate(){

        if(StepCountPresent)
        {
            return BaseUtils.bytes2ToInt(average_step_rate, 0);
        }else{
            return 0;
        }
    }

    public int getStrideCount(){
        if(strideCount)
        {
            return BaseUtils.bytes2ToInt(stride_count, 0);
        }else{
            return 0;
        }
    }

    public int getPositiveElevationGain(){
        if(elevationGain)
        {
            return BaseUtils.bytes2ToInt(positive_elevation_gain, 0);
        }else{
            return 0;
        }
    }
    public int getNegativeElevationGain(){
        if(elevationGain)
        {
            return BaseUtils.bytes2ToInt(negative_elevation_gain, 0);
        }else{
            return 0;
        }
    }

    public int getInclination()
    {
        if(inclinationAndRampAngleSetting)
        {
            return BaseUtils.bytes2ToInt(inclination, 0);
        }else{
            return 0;
        }
    }
    public int getRampAngleSetting()
    {
        if(inclinationAndRampAngleSetting)
        {
            return BaseUtils.bytes2ToInt(ramp_angle_setting, 0);
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

    public int getMetabolicEquivalent()
    {
        if(metabolic)
        {
            return BaseUtils.byte1ToInt(metabolic_equivalent[0]);
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

    public String getMovementDirection(){
        if (movementDirection){
            return "Backward";
        }else {
            return "Forward";
        }
    }

    public String convert2String(){
        return    "instant speed is <font color='red'>" + getInstantaneousSpeed()
                + "</font> km/h ;<br/> average speed is <font color='red'>" + getAverageSpeed()
                + "</font> km/h ;<br/> total distance is <font color='red'>" + getTotalDistance()
                + "</font> m ;<br/> TotalEnergy is <font color='red'>" + getTotalEnergy()
                + "</font> Calorie ;<br/> inclination is <font color='red'>" + getInclination()
                + "</font> percentage ;<br/> HeartRate is <font color='red'>" + getHeartRate()
                + "</font> bpm ;<br/> elapsed time is <font color='red'>" + getElapedTime()
                + "</font> second ;<br/> MovementDirection is <font color='red'> " + getMovementDirection()
                + "</font>;<br/> StepPerMinute is <font color='red'>" + getStepPerMinute()
                + "</font>;<br/> StrideCount is <font color='red'>" + getStrideCount()
                + "</font>;<br/> AverageStepRate is <font color='red'>"+ getAverageStepRate()
                + "</font>;<br/> ResistanceLevel is <font color='red'>"+ getResistanceLevel()
                + "</font>;<br/> RemainingTime is <font color='red'>" + getRemainingTime()
                + "</font> second;<br/>"

                ;
    }
}
