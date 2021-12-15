package com.jht.bleconnect.common;

/**
 * Created by CHARWIN.
 */

public class StartWorkoutRequest extends BaseCmdStruct {
    public StartWorkoutRequest() {
        super(CmdType.START_WORKOUT);
    }

//	@CField(start = 10)
//	private short startSpeed_x10;
//
//	@CField(start = 12)
//	private short startIncline_x10;
//
//	@CField(start = 14)
//	private byte startResistance_x10;
//
//	@CField(start = 15)
//	private byte target;


    @CField(start = 10)
    private short programType;

    @CField(start = 12)
    private short workoutTime;

    @CField(start = 14)
    private short warmupTime;

    @CField(start = 16)
    private short cooldownTime;

    @CField(start = 18)
    private byte unit;


    @CField(start = 19)
    private short startSpeed_x10;

    @CField(start = 21)
    private short startIncline_x10;

    @CField(start = 23)
    private byte startLevel;


    public short getProgramType() {
        return programType;
    }

    public void setProgramType(short programType) {
        this.programType = programType;
    }

    public short getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(short workoutTime) {
        this.workoutTime = workoutTime;
    }

    public short getWarmupTime() {
        return warmupTime;
    }

    public void setWarmupTime(short warmupTime) {
        this.warmupTime = warmupTime;
    }

    public short getCooldownTime() {
        return cooldownTime;
    }

    public void setCooldownTime(short cooldownTime) {
        this.cooldownTime = cooldownTime;
    }

    public byte getUnit() {
        return unit;
    }

    public void setUnit(byte unit) {
        this.unit = unit;
    }

    public short getStartSpeed_x10() {
        return startSpeed_x10;
    }

    public void setStartSpeed_x10(short startSpeed_x10) {
        this.startSpeed_x10 = startSpeed_x10;
    }

    public short getStartIncline_x10() {
        return startIncline_x10;
    }

    public void setStartIncline_x10(short startIncline_x10) {
        this.startIncline_x10 = startIncline_x10;
    }

    public byte getStartLevel() {
        return startLevel;
    }

    public void setStartLevel(byte startLevel) {
        this.startLevel = startLevel;
    }
}
