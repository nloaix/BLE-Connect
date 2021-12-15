package com.jht.bleconnect.entity;

public class FitnessMachineFeature {
    private final String TAG = "SupportedSpeedRange";
    private static FitnessMachineFeature data;
    //Fitness Machine Features Field
    private boolean isAverageSpeedSupported;
    private boolean isCadenceSupported;
    private boolean isTotalDistanceSupported;
    private boolean isInclinationSupported;
    private boolean isElevationGainSupported;
    private boolean isPaceSupported;
    private boolean isStepCountSupported;
    private boolean isResistanceLevelSupported;
    private boolean isStrideCountSupported;
    private boolean isExpendedEnergySupported;
    private boolean isHeartRateMeasurementSupported;
    private boolean isMetabolicEquivalentSupported;
    private boolean isElapsedTimeSupported;
    private boolean isRemainingTimeSupported;
    private boolean isPowerMeasurementSupported;
    private boolean isForceOnBeltAndPowerOutputSupported;
    private boolean isUserDataRetentionSupported;
    //Target Setting Features Field
    private boolean isSpeedTargetSettingSupported;
    private boolean isInclinationTargetSettingSupported;
    private boolean isResistanceTargetSettingSupported;
    private boolean isPowerTargetSettingSupported;
    private boolean isHeartRateTargetSettingSupported;
    private boolean isTargetedExpendedEnergyConfigurationSupported;
    private boolean isTargetedStepNumberConfigurationSupported;
    private boolean isTargetedStrideNumberConfigurationSupported;
    private boolean isTargetedDistanceConfigurationSupported;
    private boolean isTargetedTrainingTimeConfigurationSupported;
    private boolean isTargetedTimeInTwoHeartRateZonesConfigurationSupported;
    private boolean isTargetedTimeInThreeHeartRateZonesConfigurationSupported;
    private boolean isTargetedTimeInFiveHeartRateZonesConfigurationSupported;
    private boolean isIndoorBikeSimulationParametersSupported;
    private boolean isWheelCircumferenceConfigurationSupported;
    private boolean isSpinDownControlSupported;
    private boolean isTargetedCadenceConfigurationSupported;

    private byte[] flags = new byte[8];

    public static FitnessMachineFeature getInstance()
    {
        data = new FitnessMachineFeature();
        return data;
    }
    private FitnessMachineFeature(){}

    public void parseData(byte[] buffer)
    {
        System.arraycopy(buffer, 0, flags, 0, buffer.length);
        //Fitness Machine Features Field
        isAverageSpeedSupported = ((buffer[0] & 0x01) == 0x01);
        isCadenceSupported = ((buffer[0] & 0x02) == 0x02);
        isTotalDistanceSupported = ((buffer[0] & 0x04) == 0x04);
        isInclinationSupported = ((buffer[0] & 0x08) == 0x08);
        isElevationGainSupported = ((buffer[0] & 0x10) == 0x10);
        isPaceSupported =  ((buffer[0] & 0x20) == 0x20);
        isStepCountSupported = ((buffer[0] & 0x40) == 0x40);
        isResistanceLevelSupported = ((buffer[0] & 0x80) == 0x80);

        isStrideCountSupported = ((buffer[1] & 0x01) == 0x01);
        isExpendedEnergySupported = ((buffer[1] & 0x02) == 0x02);
        isHeartRateMeasurementSupported = ((buffer[1] & 0x04) == 0x04);
        isMetabolicEquivalentSupported = ((buffer[1] & 0x08) == 0x08);
        isElapsedTimeSupported = ((buffer[1] & 0x10) == 0x10);
        isRemainingTimeSupported =  ((buffer[1] & 0x20) == 0x20);
        isPowerMeasurementSupported = ((buffer[1] & 0x40) == 0x40);
        isForceOnBeltAndPowerOutputSupported = ((buffer[1] & 0x80) == 0x80);

        isUserDataRetentionSupported = ((buffer[2] & 0x01) == 0x01);

        //Target Setting Features Field
        isSpeedTargetSettingSupported = ((buffer[4] & 0x01) == 0x01);
        isInclinationTargetSettingSupported = ((buffer[4] & 0x02) == 0x02);
        isResistanceTargetSettingSupported = ((buffer[4] & 0x04) == 0x04);
        isPowerTargetSettingSupported = ((buffer[4] & 0x08) == 0x08);
        isHeartRateTargetSettingSupported = ((buffer[4] & 0x10) == 0x10);
        isTargetedExpendedEnergyConfigurationSupported =  ((buffer[4] & 0x20) == 0x20);
        isTargetedStepNumberConfigurationSupported = ((buffer[4] & 0x40) == 0x40);
        isTargetedStrideNumberConfigurationSupported = ((buffer[4] & 0x80) == 0x80);

        isTargetedDistanceConfigurationSupported = ((buffer[5] & 0x01) == 0x01);
        isTargetedTrainingTimeConfigurationSupported = ((buffer[5] & 0x02) == 0x02);
        isTargetedTimeInTwoHeartRateZonesConfigurationSupported = ((buffer[5] & 0x04) == 0x04);
        isTargetedTimeInThreeHeartRateZonesConfigurationSupported = ((buffer[5] & 0x08) == 0x08);
        isTargetedTimeInFiveHeartRateZonesConfigurationSupported = ((buffer[5] & 0x10) == 0x10);
        isIndoorBikeSimulationParametersSupported =  ((buffer[5] & 0x20) == 0x20);
        isWheelCircumferenceConfigurationSupported = ((buffer[5] & 0x40) == 0x40);
        isSpinDownControlSupported = ((buffer[5] & 0x80) == 0x80);

        isTargetedCadenceConfigurationSupported = ((buffer[6] & 0x01) == 0x01);
    }

    public boolean isAverageSpeedSupported() {
        return isAverageSpeedSupported;
    }

    public boolean isCadenceSupported() {
        return isCadenceSupported;
    }

    public boolean isTotalDistanceSupported() {
        return isTotalDistanceSupported;
    }

    public boolean isInclinationSupported() {
        return isInclinationSupported;
    }

    public boolean isElevationGainSupported() {
        return isElevationGainSupported;
    }

    public boolean isPaceSupported() {
        return isPaceSupported;
    }

    public boolean isStepCountSupported() {
        return isStepCountSupported;
    }

    public boolean isResistanceLevelSupported() {
        return isResistanceLevelSupported;
    }

    public boolean isStrideCountSupported() {
        return isStrideCountSupported;
    }

    public boolean isExpendedEnergySupported() {
        return isExpendedEnergySupported;
    }

    public boolean isHeartRateMeasurementSupported() {
        return isHeartRateMeasurementSupported;
    }

    public boolean isMetabolicEquivalentSupported() {
        return isMetabolicEquivalentSupported;
    }

    public boolean isElapsedTimeSupported() {
        return isElapsedTimeSupported;
    }

    public boolean isRemainingTimeSupported() {
        return isRemainingTimeSupported;
    }

    public boolean isPowerMeasurementSupported() {
        return isPowerMeasurementSupported;
    }

    public boolean isForceOnBeltAndPowerOutputSupported() {
        return isForceOnBeltAndPowerOutputSupported;
    }

    public boolean isUserDataRetentionSupported() {
        return isUserDataRetentionSupported;
    }

    public boolean isSpeedTargetSettingSupported() {
        return isSpeedTargetSettingSupported;
    }

    public boolean isInclinationTargetSettingSupported() {
        return isInclinationTargetSettingSupported;
    }

    public boolean isResistanceTargetSettingSupported() {
        return isResistanceTargetSettingSupported;
    }

    public boolean isPowerTargetSettingSupported() {
        return isPowerTargetSettingSupported;
    }

    public boolean isHeartRateTargetSettingSupported() {
        return isHeartRateTargetSettingSupported;
    }

    public boolean isTargetedExpendedEnergyConfigurationSupported() {
        return isTargetedExpendedEnergyConfigurationSupported;
    }

    public boolean isTargetedStepNumberConfigurationSupported() {
        return isTargetedStepNumberConfigurationSupported;
    }

    public boolean isTargetedStrideNumberConfigurationSupported() {
        return isTargetedStrideNumberConfigurationSupported;
    }

    public boolean isTargetedDistanceConfigurationSupported() {
        return isTargetedDistanceConfigurationSupported;
    }

    public boolean isTargetedTrainingTimeConfigurationSupported() {
        return isTargetedTrainingTimeConfigurationSupported;
    }

    public boolean isTargetedTimeInTwoHeartRateZonesConfigurationSupported() {
        return isTargetedTimeInTwoHeartRateZonesConfigurationSupported;
    }

    public boolean isTargetedTimeInThreeHeartRateZonesConfigurationSupported() {
        return isTargetedTimeInThreeHeartRateZonesConfigurationSupported;
    }

    public boolean isTargetedTimeInFiveHeartRateZonesConfigurationSupported() {
        return isTargetedTimeInFiveHeartRateZonesConfigurationSupported;
    }

    public boolean isIndoorBikeSimulationParametersSupported() {
        return isIndoorBikeSimulationParametersSupported;
    }

    public boolean isWheelCircumferenceConfigurationSupported() {
        return isWheelCircumferenceConfigurationSupported;
    }

    public boolean isSpinDownControlSupported() {
        return isSpinDownControlSupported;
    }

    public boolean isTargetedCadenceConfigurationSupported() {
        return isTargetedCadenceConfigurationSupported;
    }
}
