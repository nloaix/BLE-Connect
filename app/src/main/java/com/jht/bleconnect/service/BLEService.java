package com.jht.bleconnect.service;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.jht.bleconnect.App;
import com.jht.bleconnect.common.AttributeLookup;
import com.jht.bleconnect.common.BaseUtils;
import com.jht.bleconnect.common.CmdUtil;
import com.jht.bleconnect.common.DeviceController;
import com.jht.bleconnect.entity.CrossTrainerData;
import com.jht.bleconnect.entity.FTMSControlPointResponse;
import com.jht.bleconnect.entity.FitnessMachineFeature;
import com.jht.bleconnect.entity.FitnessMachineStatus;
import com.jht.bleconnect.entity.IndoorBikeData;
import com.jht.bleconnect.entity.RowerData;
import com.jht.bleconnect.entity.StairClimberData;
import com.jht.bleconnect.entity.StepClimberData;
import com.jht.bleconnect.entity.SupportedHeartRateRange;
import com.jht.bleconnect.entity.SupportedInclinationRange;
import com.jht.bleconnect.entity.SupportedPowerRange;
import com.jht.bleconnect.entity.SupportedResistanceRange;
import com.jht.bleconnect.entity.SupportedSpeedRange;
import com.jht.bleconnect.entity.TrainingStatus;
import com.jht.bleconnect.entity.TreadmillData;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BLEService extends Service {
    private String TAG = getClass().getSimpleName();

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "com.jht.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.jht.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.jht.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.jht.ble.ACTION_DATA_AVAILABLE";
    public final static String ACTION_WRITE_RESPONSE = "com.jht.ble.ACTION_WRITE_RESPONSE";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART =
            "com.nordicsemi.nrfUART.DEVICE_DOES_NOT_SUPPORT_UART";

    public final static String EXTRA_DATA = "com.jht.ble.EXTRA_DATA";

//    public static final UUID RX_SERVICE_UUID = UUID.fromString ("00003dd0-65d0-4e20-b56a-e493541ba4e2");
//    public static final UUID RX_CHAR_UUID = UUID.fromString ("00003dd1-65d0-4e20-b56a-e493541ba4e2");
//    public static final UUID TX_CHAR_UUID = UUID.fromString ("00003dd2-65d0-4e20-b56a-e493541ba4e2");

    public static final UUID RX_SERVICE_UUID = UUID.fromString ("49535343-fe7d-4ae5-8fa9-9fafd205e455");
//    public static final UUID RX_CHAR_UUID = UUID.fromString ("49535343-1e4d-4bd9-ba61-23c647249616");
    public static final UUID RX_CHAR_UUID = UUID.fromString ("49535343-8841-43f4-a8d4-ecbe34729bb3");
    public static final UUID TX_CHAR_UUID = UUID.fromString ("49535343-1e4d-4bd9-ba61-23c647249616");
//    public static final UUID TX_CHAR_UUID = UUID.fromString ("49535343-8841-43f4-a8d4-ecbe34729bb3");

    //G36
//    public static final UUID RX_SERVICE_UUID = UUID.fromString ("0000fff0-0000-1000-8000-00805f9b34fb");
//    public static final UUID RX_CHAR_UUID = UUID.fromString ("0000fff2-0000-1000-8000-00805f9b34fb");  // write
//    public static final UUID TX_CHAR_UUID = UUID.fromString ("0000fff1-0000-1000-8000-00805f9b34fb");  // notify

    public final static UUID UUID_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private int mConnectionState = STATE_DISCONNECTED;
    private BluetoothDevice currentDevice = null;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mCurrentBluetoothGatt;
    private AttributeLookup attributeLookup = new AttributeLookup(App.getAppContext());

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public BLEService() {
    }


    public boolean writeRXCharacteristic (byte[] value) {
        BluetoothGattService RxService = mCurrentBluetoothGatt.getService (RX_SERVICE_UUID);
        showMessage ("mBluetoothGatt null" + mCurrentGattServices);
        Log.d(TAG,"RXSERVICE"+RxService);
        if (RxService == null)
        {
            showMessage ("Rx service not found!");
            broadcastUpdate (DEVICE_DOES_NOT_SUPPORT_UART);
            return true;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic (RX_CHAR_UUID);
        if (RxChar == null)
        {
            showMessage ("Rx charateristic not found!");
            broadcastUpdate (DEVICE_DOES_NOT_SUPPORT_UART);
            return true;
        }
        RxChar.setValue (value);
        boolean status = mCurrentBluetoothGatt.writeCharacteristic (RxChar);
        Log.d(TAG,"STATUS"+status);
        return status;
    }

    private void showMessage (String msg)
    {
        Log.e (TAG, msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        currentDevice = (BluetoothDevice) intent.getParcelableExtra("device");
        mCurrentBluetoothGatt = currentDevice.connectGatt(this, true, mGattCallback);
        Log.i(TAG, "onBind: mCurrentBluetoothGatt ==> " + mCurrentBluetoothGatt);
        return new BleBinder();
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + mCurrentBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            int charaProp = descriptor.getCharacteristic().getProperties();
            if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                byte[] data = null;
                if (!"".equals(mCurrentParams)) {
                    Log.i(TAG, "writeCharacteristic: 0x08");
                    data = new byte[initDataLength(mCurrentOperation)];
                }
                if(data != null){
                    if(mCurrentOperation == 0x0c) {
                        data = new byte[4];
                        data[0] = (byte)mCurrentOperation;
                        int data16 = Integer.parseInt(mCurrentParams, 10);
                        String unsignedString = Integer.toUnsignedString(data16, 16);
                        int num = 6 - unsignedString.length();
                        for(int i = 0; i<num;i++){
                            unsignedString = "0"+ unsignedString;
                        }
                        data[3] = (byte)Integer.parseInt(unsignedString.substring(0,2), 16);
                        data[2] = (byte)Integer.parseInt(unsignedString.substring(2,4), 16);
                        data[1] = (byte)Integer.parseInt(unsignedString.substring(4), 16);
                        currentWriteCharacteristicFromStrUUID.setValue(data);
                        gatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID);
                        return;
                    }
                    if (data.length ==2 ){
                        data[0] = (byte)mCurrentOperation;
                        data[1] = (byte)Integer.parseInt(mCurrentParams, 10);
                        currentWriteCharacteristicFromStrUUID.setValue(data);
                    } else if(data.length == 3) {
                        data[0] = (byte) mCurrentOperation;
                        int data16 = Integer.parseInt(mCurrentParams, 10);
                        String unsignedString = Integer.toUnsignedString(data16, 16);
                        if (unsignedString.length() <= 2) {
                            data[2] = 0;
                            data[1] = (byte) Integer.parseInt(unsignedString, 16);
                            Log.i(TAG, "onDescriptorWrite: unsignedString.length() < 2 || data[2] = " + data[2]);
                        }
                        if (unsignedString.length() == 3) {
                            data[2] = (byte) Integer.parseInt(unsignedString.substring(0, 1), 16);
                            data[1] = (byte) Integer.parseInt(unsignedString.substring(1), 16);
                        }
                        if (unsignedString.length() == 4) {
                            data[2] = (byte) Integer.parseInt(unsignedString.substring(0, 2), 16);
                            data[1] = (byte) Integer.parseInt(unsignedString.substring(2), 16);
                        }
                        currentWriteCharacteristicFromStrUUID.setValue(data);
                    } else {
                        currentWriteCharacteristicFromStrUUID.setValue(mCurrentOperation, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    }
                }else {
                    currentWriteCharacteristicFromStrUUID.setValue(mCurrentOperation, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                }
                gatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "onCharacteristicRead: 1111");
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_WRITE_RESPONSE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged: " + characteristic.getUuid().toString());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        UUID characteristicUUID = attributeLookup.getCharacteristicUUID(attributeLookup.getCharacteristic(characteristic.getUuid()));
        Log.d(TAG,"UUID======="+characteristicUUID);
        Log.d(TAG,"UUID======="+characteristic.getUuid());
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
            sendBroadcast(intent);
            return;
        }
        if(UUID.fromString("00002a26-0000-1000-8000-00805F9B34FB").equals(characteristic.getUuid()) || UUID.fromString("00002a27-0000-1000-8000-00805F9B34FB").equals(characteristic.getUuid()) || UUID.fromString("00002a28-0000-1000-8000-00805F9B34FB").equals(characteristic.getUuid())){
            Log.i(TAG, "broadcastUpdate: revision");
            byte[] value = characteristic.getValue();
            intent.putExtra(EXTRA_DATA, new String(value, StandardCharsets.UTF_8));
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID == null) {
            intent.putExtra(EXTRA_DATA, "current characteristic can't be parsed data");
            sendBroadcast(intent);
            return;
        }
        // so characteristicUUID != null
        if (characteristicUUID.equals(UUID.fromString("00002ad2-0000-1000-8000-00805F9B34FB"))) {
            //INDOOR_BIKE_DATA
            IndoorBikeData indoorBikeData = IndoorBikeData.getInstance();
            indoorBikeData.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "instant speed is <font color='red'>" + indoorBikeData.getInstantaneousSpeed()
                    + "</font> km/h ;<br/> average speed is <font color='red'>" + indoorBikeData.getAverageSpeed()
                    + "</font> km/h ;<br/> Instantaneous Cadence is <font color='red'>" + indoorBikeData.getInstantaneousCadence()
                    + "</font> rpm ;<br/> average cadence is <font color='red'>" + indoorBikeData.getAverageCadence()
                    + "</font> rpm ;<br/> Total Energy is <font color='red'>" + indoorBikeData.getTotalEnergy()
                    + "</font> Calorie ;<br/> Energy Per Hour is <font color='red'>" + indoorBikeData.getEnergyPerHour()
                    + "</font> Calorie ;<br/> Energy Per Minute is <font color='red'>" + indoorBikeData.getEnergyPerMinute()
                    + "</font> Calorie ;<br/> Heart Rate is <font color='red'>" + indoorBikeData.getHeartRate()
                    + "</font> BPM ;<br/> Metabolic Equivalent is <font color='red'>" + indoorBikeData.getMetabolicEquivalent()
                    + "</font> metabolic_equivalent ;<br/> RemainingTime is <font color='red'>" + indoorBikeData.getRemainingTime()
                    + "</font> second ;<br/> total distance is <font color='red'>" + indoorBikeData.getTotalDistance()
                    + "</font> metre ;<br/> resistance level is <font color='red'>" + indoorBikeData.getResistanceLevel()
                    + "</font>;<br/> instant power is <font color='red'>" + indoorBikeData.getInstantaneousPower()
                    + "</font> watt ;<br/> average power is <font color='red'>" + indoorBikeData.getAveragePower()
                    + "</font> watt ;<br/> elapsed time is <font color='red'>" + indoorBikeData.getElapedTime() + "</font> second ;<br/>");
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002acc-0000-1000-8000-00805F9B34FB"))) {
            //Fitness Machine feature
            Log.i(TAG, "broadcastUpdate: 2acc");
            FitnessMachineFeature fitnessMachineFeature = FitnessMachineFeature.getInstance();
            fitnessMachineFeature.parseData(characteristic.getValue());
            intent.putExtra("FitnessMachineFeature","flag");
            intent.putExtra(EXTRA_DATA, "Fitness Machine Features Field : "
                    + "<br/>         is Average Speed Supported <font color='red'>" + fitnessMachineFeature.isAverageSpeedSupported()
                    + "</font>;<br/> is Cadence Supported <font color='red'>" + fitnessMachineFeature.isCadenceSupported()
                    + "</font>;<br/> is Total Distance Supported <font color='red'>" + fitnessMachineFeature.isTotalDistanceSupported()
                    + "</font>;<br/> is Inclination Supported <font color='red'>" + fitnessMachineFeature.isInclinationSupported()
                    + "</font>;<br/> is Elevation Gain Supported <font color='red'>" + fitnessMachineFeature.isElevationGainSupported()
                    + "</font>;<br/> is Pace Supported <font color='red'>" + fitnessMachineFeature.isPaceSupported()
                    + "</font>;<br/> is Step Count Supported <font color='red'>" + fitnessMachineFeature.isStepCountSupported()
                    + "</font>;<br/> is Resistance Level Supported <font color='red'>" + fitnessMachineFeature.isResistanceLevelSupported()
                    + "</font>;<br/> is Stride Count Supported <font color='red'>" + fitnessMachineFeature.isStrideCountSupported()
                    + "</font>;<br/> is Expended Energy Supported <font color='red'>" + fitnessMachineFeature.isExpendedEnergySupported()
                    + "</font>;<br/> is HeartRate Measurement Supported <font color='red'>" + fitnessMachineFeature.isHeartRateMeasurementSupported()
                    + "</font>;<br/> is Metabolic Equivalent Supported <font color='red'>" + fitnessMachineFeature.isMetabolicEquivalentSupported()
                    + "</font>;<br/> is Elapsed Time Supported <font color='red'>" + fitnessMachineFeature.isElapsedTimeSupported()
                    + "</font>;<br/> is Remaining Time Supported <font color='red'>" + fitnessMachineFeature.isRemainingTimeSupported()
                    + "</font>;<br/> is Power Measurement Supported <font color='red'>" + fitnessMachineFeature.isPowerMeasurementSupported()
                    + "</font>;<br/> is Force On Belt And Power Output Supported <font color='red'>" + fitnessMachineFeature.isForceOnBeltAndPowerOutputSupported()
                    + "</font>;<br/> is User Data Retention Supported <font color='red'>" + fitnessMachineFeature.isUserDataRetentionSupported()
                    + "</font> <br/>Target Setting Features Field: "
                    + "<br/>         is Speed Target Setting Supported <font color='red'>" + fitnessMachineFeature.isSpeedTargetSettingSupported()
                    + "</font>;<br/> is Inclination Target Setting Supported <font color='red'>" + fitnessMachineFeature.isInclinationTargetSettingSupported()
                    + "</font>;<br/> is Resistance Target Setting Supported <font color='red'>" + fitnessMachineFeature.isResistanceTargetSettingSupported()
                    + "</font>;<br/> is PowerTarget Setting Supported <font color='red'>" + fitnessMachineFeature.isPowerTargetSettingSupported()
                    + "</font>;<br/> is HeartRate Target Setting Supported <font color='red'>" + fitnessMachineFeature.isHeartRateTargetSettingSupported()
                    + "</font>;<br/> is Targeted Expended Energy Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedExpendedEnergyConfigurationSupported()
                    + "</font>;<br/> is Targeted Step Number Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedStepNumberConfigurationSupported()
                    + "</font>;<br/> is Targeted Stride Number Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedStrideNumberConfigurationSupported()
                    + "</font>;<br/> is Targeted Distance Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedDistanceConfigurationSupported()
                    + "</font>;<br/> is Targeted Training Time Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedTrainingTimeConfigurationSupported()
                    + "</font>;<br/> is Targeted Time In Two HeartRate Zones Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedTimeInTwoHeartRateZonesConfigurationSupported()
                    + "</font>;<br/> is Targeted Time In Three HeartRate Zones Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedTimeInThreeHeartRateZonesConfigurationSupported()
                    + "</font>;<br/> is Targeted Time In Five HeartRate Zones Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedTimeInFiveHeartRateZonesConfigurationSupported()
                    + "</font>;<br/> is IndoorBike Simulation Parameters Supported <font color='red'>" + fitnessMachineFeature.isIndoorBikeSimulationParametersSupported()
                    + "</font>;<br/> is Wheel Circumference Configuration Supported <font color='red'>" + fitnessMachineFeature.isWheelCircumferenceConfigurationSupported()
                    + "</font>;<br/> is Spin Down Control Supported <font color='red'>" + fitnessMachineFeature.isSpinDownControlSupported()
                    + "</font>;<br/> is Targeted Cadence Configuration Supported <font color='red'>" + fitnessMachineFeature.isTargetedCadenceConfigurationSupported()
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002acd-0000-1000-8000-00805F9B34FB"))) {
            //Treadmill Data
            Log.i(TAG, "broadcastUpdate: 2acd");
            TreadmillData treadmillData = TreadmillData.getInstance();
            treadmillData.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "instant speed is <font color='red'>" + treadmillData.getInstantaneousSpeed()
                    + "</font> km/h ;<br/> average speed is <font color='red'>" + treadmillData.getAverageSpeed()
                    + "</font> km/h ;<br/> Ramp_Angle_Setting is <font color='red'>" + treadmillData.getRamp_Angle_Setting()
                    + "</font> degree ;<br/> total distance is <font color='red'>" + treadmillData.getTotalDistance()
                    + "</font> m ;<br/> TotalEnergy is <font color='red'>" + treadmillData.getTotalEnergy()
                    + "</font> Calorie ;<br/> inclination is <font color='red'>" + treadmillData.getinclination()
                    + "</font> percentage ;<br/> HeartRate is <font color='red'>" + treadmillData.getHeartRate()
                    + "</font> bpm ;<br/> elapsed time is <font color='red'>" + treadmillData.getElapedTime()
                    + "</font> second ;<br/> Average_pace is <font color='red'>" + treadmillData.getAverage_pace()
                    + "</font> kilometre_per_minute ;<br/> InstantaneousPace is <font color='red'>" + treadmillData.getInstantaneousPace()
                    + "</font> kilometre_per_minute; <br/> Positive_elevation_gain is <font color='red'>" + treadmillData.getPositive_elevation_gain()
                    + "</font> metre ;<br/> Negative_elevation_gain is <font color='red'>" + treadmillData.getNegative_elevation_gain() + "</font> metre ;<br/>");
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad9-0000-1000-8000-00805F9B34FB"))) {
            FTMSControlPointResponse instance = FTMSControlPointResponse.getInstance(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "ResponseCode_OpCode : " + instance.getResponseCode_OpCode()
                    + "<br/> RequestOpCode : " + instance.getRequestOpCode()
                    + "<br/> ResultCode : " + instance.getResultCode()
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad4-0000-1000-8000-00805F9B34FB"))) {
            //Supported speed Range | kilometre_per_hour
            SupportedSpeedRange instance = SupportedSpeedRange.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "MinimumSpeed : " + instance.getMinimumSpeed()
                    + " km/h <br/> MaximumSpeed : " + instance.getMaximumSpeed()
                    + " km/h <br/> MinimumIncrement : " + instance.getMinimumIncrement() + " km/h <br/> "
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad5-0000-1000-8000-00805F9B34FB"))) {
            //Supported Inclination Range | %
            SupportedInclinationRange instance = SupportedInclinationRange.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "MinimumSpeed : " + instance.getMinimumInclination()
                    + " % <br/> MaximumSpeed : " + instance.getMaximumInclination()
                    + " % <br/> MinimumIncrement : " + instance.getMinimumIncrement() + " % <br/> "
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad6-0000-1000-8000-00805F9B34FB"))) {
            //Supported Resistance Range |
            SupportedResistanceRange instance = SupportedResistanceRange.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "MinimumResistanceLevel : " + instance.getMinimumResistanceLevel()
                    + " <br/> MaximumResistanceLevel : " + instance.getMaximumResistanceLevel()
                    + " <br/> MinimumIncrement : " + instance.getMinimumIncrement() + " <br/> "
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad7-0000-1000-8000-00805F9B34FB"))) {
            //Supported Heart Rate Range | bpm
            SupportedHeartRateRange instance = SupportedHeartRateRange.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "MinimumHeartRate : " + instance.getMinimumHeartRate()
                    + " bpm <br/> MaximumHeartRate : " + instance.getMaximumHeartRate()
                    + " bpm <br/> MinimumIncrement : " + instance.getMinimumIncrement() + " bpm<br/> "
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad8-0000-1000-8000-00805F9B34FB"))) {
            //Supported Power Range | watt
            SupportedPowerRange instance = SupportedPowerRange.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, "MinimumPower : " + instance.getMinimumPower()
                    + " watt <br/> MaximumPower : " + instance.getMaximumPower()
                    + " watt <br/> MinimumIncrement : " + instance.getMinimumIncrement() + " watt<br/> "
            );
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad1-0000-1000-8000-00805F9B34FB"))) {
            //Rower Data
            RowerData instance = RowerData.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, instance.convert2String());
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ace-0000-1000-8000-00805F9B34FB"))) {
            //Cross Trainer Data
            CrossTrainerData instance = CrossTrainerData.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, instance.convert2String());
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad0-0000-1000-8000-00805F9B34FB"))) {
            //Stair Climber Data
            StairClimberData instance = StairClimberData.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, instance.convert2String());
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002acf-0000-1000-8000-00805F9B34FB"))) {
            //Step Climber Data
            StepClimberData instance = StepClimberData.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, instance.convert2String());
            sendBroadcast(intent);
            return;
        }
        if (characteristicUUID.equals(UUID.fromString("00002ad3-0000-1000-8000-00805F9B34FB"))) {
            //TrainingStatus
            TrainingStatus instance = TrainingStatus.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, instance.getTrainingStatus());
            sendBroadcast(intent);
            return;
        }

        if (characteristicUUID.equals(UUID.fromString("00002ada-0000-1000-8000-00805F9B34FB"))) {
            //FitnessMachineStatus
            FitnessMachineStatus instance = FitnessMachineStatus.getInstance();
            instance.parseData(characteristic.getValue());
            intent.putExtra(EXTRA_DATA, instance.getCurrentTrainingStatus());
            sendBroadcast(intent);
            return;
        }
        // For all other profiles, writes the data formatted in HEX.
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.i(TAG, "broadcastUpdate: ==> " + new String(data));
            intent.putExtra(EXTRA_DATA, new String(data) + stringBuilder.toString());
        }
        sendBroadcast(intent);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public class BleBinder extends Binder {

        public BLEService getBleService() {
            return BLEService.this;
        }
    }

    private List<BluetoothGattService> mCurrentGattServices = null;
    private Map<BluetoothGattService, List<BluetoothGattCharacteristic>> mCurrentServiceAndCharacteristic = null;
    private Map<String, BluetoothGattCharacteristic> mAllGattCharacteristic = null;

    public List<BluetoothGattService> getCurrentGattServices() {
        mCurrentGattServices = mCurrentBluetoothGatt.getServices();
        mCurrentServiceAndCharacteristic = new HashMap<>();
        mAllGattCharacteristic = new HashMap<>();
        for (BluetoothGattService gattService : mCurrentGattServices) {
            List<BluetoothGattCharacteristic> characteristics = gattService.getCharacteristics();
            mCurrentServiceAndCharacteristic.put(gattService, characteristics);
            for (BluetoothGattCharacteristic characteristic : characteristics) {
                mAllGattCharacteristic.put(characteristic.getUuid().toString(), characteristic);
            }
        }
        return mCurrentGattServices;
    }

    public void disconnect() {
        mCurrentBluetoothGatt.disconnect();
        mCurrentGattServices = null;
        mCurrentServiceAndCharacteristic = null;
        mAllGattCharacteristic = null;
        mCurrentReadCharacteristicFromStrUUID = null;
        currentNotifyCharacteristicFromStrUUID = null;
        currentWriteCharacteristicFromStrUUID = null;
    }


    private volatile BluetoothGattCharacteristic mCurrentReadCharacteristicFromStrUUID = null;
    private volatile BluetoothGattCharacteristic currentNotifyCharacteristicFromStrUUID = null;
    private volatile BluetoothGattCharacteristic currentWriteCharacteristicFromStrUUID = null;
    private volatile int mCurrentOperation;
    private volatile String mCurrentParams = "";

    public void readCharacteristic(String characteristic_uuid) {
        if (currentNotifyCharacteristicFromStrUUID != null) {
            setCharacteristicNotification(currentNotifyCharacteristicFromStrUUID.getUuid().toString(), false);
        }
        mCurrentReadCharacteristicFromStrUUID = getCurrentCharacteristicFromStrUUID(characteristic_uuid);
        mCurrentBluetoothGatt.readCharacteristic(mCurrentReadCharacteristicFromStrUUID);
    }

    public void setCharacteristicNotification(String characteristic_uuid, boolean enabled) {
        if (enabled) {
            if (currentNotifyCharacteristicFromStrUUID != null) {
                mCurrentBluetoothGatt.setCharacteristicNotification(currentNotifyCharacteristicFromStrUUID, false);
            }
        }
        currentNotifyCharacteristicFromStrUUID = getCurrentCharacteristicFromStrUUID(characteristic_uuid);
        BluetoothGattDescriptor descriptor = currentNotifyCharacteristicFromStrUUID.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        if (enabled) {
            mCurrentBluetoothGatt.setCharacteristicNotification(currentNotifyCharacteristicFromStrUUID, true);
            Log.i(TAG, "setCharacteristicNotification: descriptor != null ? " + (descriptor != null));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mCurrentBluetoothGatt.writeDescriptor(descriptor);
            }
        } else {
            mCurrentBluetoothGatt.setCharacteristicNotification(currentNotifyCharacteristicFromStrUUID, false);
            currentNotifyCharacteristicFromStrUUID = null;
        }
    }

    public void writeCharacteristic(String characteristic_uuid, String strOperation, String strParameter) {
        if (currentNotifyCharacteristicFromStrUUID != null) {
            setCharacteristicNotification(currentNotifyCharacteristicFromStrUUID.getUuid().toString(), false);
        }
        currentWriteCharacteristicFromStrUUID = getCurrentCharacteristicFromStrUUID(characteristic_uuid);
        final int charaProp = currentWriteCharacteristicFromStrUUID.getProperties();
        //
        if((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0){
            Log.i(TAG, "writeCharacteristic:  BluetoothGattCharacteristic.PROPERTY_WRITE");
            //currentWriteCharacteristicFromStrUUID.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
            if (currentWriteCharacteristicFromStrUUID.getUuid().toString().contains("fff3")){
                int operation;
                try {
                    operation = Integer.parseInt(strOperation, 16);
                } catch (NumberFormatException ex) {
                    Toast.makeText(App.getAppContext(), "operation is not Correct", Toast.LENGTH_SHORT).show();
                    return;
                }
                //
                if (operation == 0x01){
                    short speed = Short.parseShort(strParameter);
                    byte[] bytes = DeviceController.setIncline(speed);
                    Log.d("cccc", "setIncline: " + CmdUtil.bytes2HexString(bytes) + " ; " + currentWriteCharacteristicFromStrUUID.getUuid().toString());
                    currentWriteCharacteristicFromStrUUID.setValue(bytes);
                    currentWriteCharacteristicFromStrUUID.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    Log.i("cccc", "setIncline: " + mCurrentBluetoothGatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID));
                    return;
                }

                if (operation == 0x02){
                    short speed = Short.parseShort(strParameter);
                    byte[] bytes = DeviceController.setSpeed(speed, true);
                    Log.d("cccc", "setSpeed: " + CmdUtil.bytes2HexString(bytes) + " ; " + currentWriteCharacteristicFromStrUUID.getUuid().toString());
                    currentWriteCharacteristicFromStrUUID.setValue(bytes);
                    currentWriteCharacteristicFromStrUUID.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    Log.i("cccc", "setSpeed: " + mCurrentBluetoothGatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID));
                    return;
                }




                //


                mCurrentParams = "";
                String name= strParameter;
                Log.i(TAG, "strParameter ==>  " + name);
                int length= name.getBytes().length;
                byte[] msg= new byte[length+3];
                msg[0]=0x0A;
                msg[1]=(byte)length;
                int i=0;
                for(;i<length;i++){
                    try {
                        msg[2+i]=name.getBytes("UTF-8")[i];
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        msg[2+i]=name.getBytes()[i];
                        Log.i(TAG,"getByte(UTF-8) error");
                    }
                }
                msg[2+i]=(byte)0xA0;
                Log.i(TAG,"setvalue==="+ currentWriteCharacteristicFromStrUUID.setValue(msg));
                mCurrentBluetoothGatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID);
                return;
            }
        }



        //
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BluetoothGattDescriptor descriptor = currentWriteCharacteristicFromStrUUID.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            mCurrentBluetoothGatt.setCharacteristicNotification(currentWriteCharacteristicFromStrUUID, true);

            int operation;
            try {
                operation = Integer.parseInt(strOperation, 16);
                mCurrentOperation = operation;
            } catch (NumberFormatException ex) {
                Toast.makeText(App.getAppContext(), "operation is not Correct", Toast.LENGTH_SHORT).show();
                return;
            }
            mCurrentParams = "";
            if (!"".equals(strParameter)) {
                mCurrentParams = strParameter;
            }

            currentNotifyCharacteristicFromStrUUID = currentWriteCharacteristicFromStrUUID;
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                mCurrentBluetoothGatt.writeDescriptor(descriptor);
            }

        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            currentWriteCharacteristicFromStrUUID.setWriteType(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE);
            int operation;
            try {
                operation = Integer.parseInt(strOperation, 16);
            } catch (NumberFormatException ex) {
                Toast.makeText(App.getAppContext(), "operation is not Correct", Toast.LENGTH_SHORT).show();
                return;
            }
            mCurrentParams = "";
            byte[] data = null;
            if (!"".equals(strParameter)) {
                data = new byte[initDataLength(operation)];
            }
            if(data != null){
                if(operation == 0x0c){
                    data = new byte[4];
                    data[0] = (byte)operation;
                    int data16 = Integer.parseInt(strParameter, 10);
                    String unsignedString = Integer.toUnsignedString(data16, 16);
                    int num = 6 - unsignedString.length();
                    for(int i = 0; i<num;i++){
                        unsignedString = "0"+ unsignedString;
                    }
                    data[3] = (byte)Integer.parseInt(unsignedString.substring(0,2), 16);
                    data[2] = (byte)Integer.parseInt(unsignedString.substring(2,4), 16);
                    data[1] = (byte)Integer.parseInt(unsignedString.substring(4), 16);
                    currentWriteCharacteristicFromStrUUID.setValue(data);
                    mCurrentBluetoothGatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID);
                    return;
                }
                if (data.length == 2 ){
                    data[0] = (byte)operation;
                    data[1] = (byte)Integer.parseInt(strParameter, 10);
                    currentWriteCharacteristicFromStrUUID.setValue(data);
                } else if(data.length == 3) {
                    data[0] = (byte)operation;
                    int data16 = Integer.parseInt(strParameter, 10);
                    String unsignedString = Integer.toUnsignedString(data16, 16);
                    if (unsignedString.length() < 2){
                        data[2] = 0;
                        data[1] = (byte)Integer.parseInt(unsignedString, 16);
                    }
                    if (unsignedString.length() == 3){
                        data[2] = (byte)Integer.parseInt(unsignedString.substring(0,1), 16);
                        data[1] = (byte)Integer.parseInt(unsignedString.substring(1), 16);
                    }
                    if (unsignedString.length() == 4){
                        data[2] = (byte)Integer.parseInt(unsignedString.substring(0,2), 16);
                        data[1] = (byte)Integer.parseInt(unsignedString.substring(2), 16);
                    }
                    currentWriteCharacteristicFromStrUUID.setValue(data);
                } else {
                    currentWriteCharacteristicFromStrUUID.setValue(operation, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                }

            }else {
                currentWriteCharacteristicFromStrUUID.setValue(operation, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            }
            mCurrentBluetoothGatt.writeCharacteristic(currentWriteCharacteristicFromStrUUID);
        }

    }

    private int  initDataLength(int currentOperation){
        switch(currentOperation){
            case 0x00:
            case 0x01:
                return 0;
            case 0x02:
            case 0x03:
                return 3;
            case 0x04:
                return 2;
            case 0x05:
                return 3;
            case 0x06:
                return 2;
            case 0x07:
                return 0;
            case 0x08:
                return 2;
            case 0x09:
                return 3;
            case 0x0A:
                return 3;
            case 0x0B:
                return 3;
            case 0x0C:
                return 4;
            case 0x0D:
                return 3;
            case 0x0E:
                return 5;
            case 0x0F:
                return 7;
            case 0x10:
                return 11;
            case 0x11:
                return 7;
            case 0x12:
                return 3;
            case 0x13:
                return 2;
            case 0x14:
                return 3;
            case 0x80:
                return 0;
            default:
                return 0;
        }

    }
    private BluetoothGattCharacteristic getCurrentCharacteristicFromStrUUID(String characteristic_uuid) {
        return mAllGattCharacteristic.get(characteristic_uuid);
    }

    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}
