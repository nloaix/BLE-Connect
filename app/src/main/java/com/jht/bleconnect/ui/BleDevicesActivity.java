package com.jht.bleconnect.ui;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jht.bleconnect.App;
import com.jht.bleconnect.R;
import com.jht.bleconnect.adapter.BTDeviceLVAdapter;
import com.jht.bleconnect.common.AppPermission;
import com.jht.bleconnect.common.AppUiUtil;
import com.jht.bleconnect.common.DialogUtil;
import com.jht.bleconnect.service.BLEService;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class BleDevicesActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private LinearLayout mLlAllSwitch;
    private LinearLayout mLlBtSwitch;
    private Button mBtnOpenBt;
    private LinearLayout mLlLocationSwitch;
    private Button mBtnOpenLocation;
    private Button mScan;
    private ListView mLvBtDevices;
    private Button begin;   // 开始按钮
    private boolean mConnected;
    private String deviceName;
    private boolean mDisconnect = true;
    private Timer timer;
    private int rssi_data = 50;  // 默认RSSI值
    private int i;
    private SeekBar seekBar;     // 滑动条
    private TextView tv_rssi_data;  // rssi值的显示


    //ui code
    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_ENABLE_GPS = 101;
    private static final int REQUEST_PERMISSIONS = 103;
    private static final int RESULT_NOT_OK = 10;
    //handler code
    public static final int UI_HIDE_BT_ENABLED = 200;
    public static final int UI_SHOW_BT_ENABLED = 201;
    public static final int UI_SHOW_OR_HIDE_GPS_ENABLED = 202;
    public static final int UI_DISMISS_DIALOG = 203;
    public static final int UI_GO_SETTINGS = 204;
    public static final int UI_STOP_BT_SCAN = 205;

    private static int SCAN_PERIOD = 5000;
    private boolean isGPSOpen = false;
    private boolean isBTOpen = false;
    private boolean mScanning = false;
    private UIHandler uiHandler = null;
    private AlertDialog alertDialog = null;
    private boolean hasPermission = false;
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private BtAndGpsReceiver btAndGpsReceiver = null;
    private BTDeviceLVAdapter btDeviceLVAdapter;
    private BluetoothGatt bluetoothGatt;
    private BLEService bleService;

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device.getName() == null || device.getName().equals("") || rssi < -rssi_data){
            } else {
                btDeviceLVAdapter.addDevice(device,rssi);
            }
        }
    };

    private Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            Toast.makeText(App.getAppContext(), "stop bt scan!!", Toast.LENGTH_SHORT).show();
            mScan.setText("SCAN");
            Log.d(TAG,"搜索到的设备总数=="+btDeviceLVAdapter.getCount());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_devices);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        uiHandler = new UIHandler(this);
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        isBTOpen = isBTOpen(bluetoothAdapter);
        isGPSOpen = isGPSOpen();
        initView();
        btAndGpsReceiver = new BtAndGpsReceiver();
        registerReceiver(btAndGpsReceiver, getFilters());
        hasPermission = AppPermission.requestAppPermissions(this, REQUEST_PERMISSIONS);
        bindView();
    }

    // 滑动条设置
    private void bindView() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        tv_rssi_data = (TextView) findViewById(R.id.tv_rssi_data);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_rssi_data.setText("-" + progress + "dBm");
                rssi_data = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 按住seekbar时触发
                mScanning = false;
                mScan.setText("SCAN");
                uiHandler.removeCallbacks(stopScan);
                bluetoothAdapter.stopLeScan(leScanCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 松开seekbar时触发
            }
        });
    }

    private void initView() {
        mLlAllSwitch = findViewById(R.id.ll_all_switch);
        mLlBtSwitch = findViewById(R.id.ll_bt_switch);
        if (isBTOpen) {
            mLlBtSwitch.setVisibility(View.GONE);
        }
        mBtnOpenBt = findViewById(R.id.btn_open_bt);
        mBtnOpenBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });
        mLlLocationSwitch = findViewById(R.id.ll_location_switch);
        if (isGPSOpen) {
            mLlLocationSwitch.setVisibility(View.GONE);
            mLlLocationSwitch.setVisibility(View.GONE);
        }
        mBtnOpenLocation = findViewById(R.id.btn_open_location);
        mBtnOpenLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_ENABLE_GPS);
            }
        });
        mScan = findViewById(R.id.scan);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission && isBTOpen && isGPSOpen) {
                    Log.i(TAG, "onClick: start scan!");
                    if (!mScanning) {
                        btDeviceLVAdapter.clearDevicesList();
                        bluetoothAdapter.startLeScan(leScanCallback);
                        mScanning = true;
                        uiHandler.postDelayed(stopScan, SCAN_PERIOD);
                        mScan.setText("Scanning");
                    } else {
                        Toast.makeText(App.getAppContext(), "bt is scanning!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    hasPermission = AppPermission.requestAppPermissions(BleDevicesActivity.this, REQUEST_PERMISSIONS);
                    if (hasPermission) {
                        Toast.makeText(App.getAppContext(), "Please open BT and GPS", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mLvBtDevices = findViewById(R.id.lv_bt_devices);
        btDeviceLVAdapter = new BTDeviceLVAdapter(App.getAppContext());
        mLvBtDevices.setAdapter(btDeviceLVAdapter);
        mLvBtDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mScanning = false;
                mScan.setText("SCAN");
                uiHandler.removeCallbacks(stopScan);
                bluetoothAdapter.stopLeScan(leScanCallback);
                BluetoothDevice device = (BluetoothDevice) btDeviceLVAdapter.getItem(position);
                Log.d(TAG,"原来点击后的device==="+device);
                Intent intent = new Intent(BleDevicesActivity.this, BleControlActivity.class);
                intent.putExtra("device", device);
                startActivity(intent);
//                Intent intent = new Intent(getApplicationContext(),BLEService.class);
//                intent.putExtra("device",device);
//                bindService(intent,serviceConnection,BIND_AUTO_CREATE);
//                registerReceiver(mGattServiceContent,getFilters());
            }
        });


        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        // 循环btDeviceLVAdapter中的LIST
        begin = (Button) findViewById(R.id.begin);
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer = new Timer();
                timer.schedule(task,0,3000);
                Log.d(TAG,"mLvBtDevices的长度是="+mLvBtDevices.getCount());
            }
        });
    }


    private Handler handler = new Handler() {
        public void handleMessage (Message msg) {
            Log.d(TAG,"此时的i=="+i);
            BluetoothDevice device = (BluetoothDevice) btDeviceLVAdapter.getItem(i);
            Log.d(TAG,"此时的device===="+device);
            Intent intent = new Intent(getApplicationContext(),BLEService.class);
            intent.putExtra("device",device);
            bindService(intent,serviceConnection,BIND_AUTO_CREATE);
            registerReceiver(mGattServiceContent,getFilters());
            i += 1;
            if (i == btDeviceLVAdapter.getCount()) {
                timer.cancel();
                Log.d(TAG,"定时器已取消");
            }
            super.handleMessage(msg);
        }
    };

    // GATT service connect
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BLEService.BleBinder) service).getBleService();
            Log.d(TAG,"onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisConnected");
            bleService = null;
        }
    };

    private final BroadcastReceiver mGattServiceContent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,"action==" + action);
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG,"ACTION_GATT_CONNECTED");
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG,"ACTION_GATT_DISCONNECTED");
            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG,"ACTION_GATT_SERVICES_DISCOVERED");
                byte[] value = {
                        (byte) 0xFE, 0x13, 0x01, 0x03, 0x15
                };
                bleService.writeRXCharacteristic(value);
                unbindService(serviceConnection);
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG,"ACTION_DATA_AVAILABLE");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(btAndGpsReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    if (bluetoothAdapter == null) {
                        throw new RuntimeException("bluetoothAdapter can not null");
                    }
                    isBTOpen = true;
                    mLlBtSwitch.setVisibility(View.GONE);
                } else {
                    isBTOpen = false;
                    mLlLocationSwitch.setVisibility(View.VISIBLE);
                }
                break;
            case REQUEST_ENABLE_GPS:
                openGPS();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0) {
                    String permissionsMsg = "please grant permission { ";
                    boolean grantResultsOK = true;
                    boolean shouldShowRequestPermission = false;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            grantResultsOK = false;
                            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    permissions[i])) {
                                shouldShowRequestPermission = true;
                                permissionsMsg += "'" + permissions[i] + "'";
                            }
                        }
                    }
                    permissionsMsg += " }";
                    if (grantResultsOK) {
                        hasPermission = true;
                    } else {
                        if (shouldShowRequestPermission) {
                            alertDialog = DialogUtil.showMsgAlertDialog(this, permissionsMsg, uiHandler);
                            alertDialog.show();
                        }
                    }
                } else {
                    hasPermission = false;
                }
            }
        }
    }

    public static String ACTION_REFRESH = "com.jht.ble.rescan";

    private class BtAndGpsReceiver extends BroadcastReceiver {
        /**
         * int STATE_OFF = 10; //bt closed
         * int STATE_ON = 12; //bt opened
         * int STATE_TURNING_OFF = 13; //bt is closing
         * int STATE_TURNING_ON = 11; //bt is opening
         *
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive == action " + action);
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(action)) {
                uiHandler.sendEmptyMessage(UI_SHOW_OR_HIDE_GPS_ENABLED);
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
                if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                    uiHandler.sendEmptyMessage(UI_SHOW_BT_ENABLED);
                } else {
                    uiHandler.sendEmptyMessage(UI_HIDE_BT_ENABLED);
                }
            } else if (ACTION_REFRESH.equals(action)) {
                if (hasPermission && isBTOpen && isGPSOpen) {
                    Log.i(TAG, "onClick: start rescan!");
                    btDeviceLVAdapter.clearDevicesList();
                    bluetoothAdapter.startLeScan(leScanCallback);
                    mScanning = true;
                    uiHandler.postDelayed(stopScan, SCAN_PERIOD);
                    mScan.setText("Scanning");

                } else {
                    hasPermission = AppPermission.requestAppPermissions(BleDevicesActivity.this, REQUEST_PERMISSIONS);
                    if (hasPermission) {
                        Toast.makeText(App.getAppContext(), "Please open BT and GPS", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }

    }

    public static class UIHandler extends Handler {
        private WeakReference<BleDevicesActivity> ref = null;

        public UIHandler(BleDevicesActivity btDevicesActivity) {
            ref = new WeakReference<BleDevicesActivity>(btDevicesActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            BleDevicesActivity btDevicesActivity = ref.get();
            switch (msg.what) {
                case UI_HIDE_BT_ENABLED:
                    btDevicesActivity.openBT(RESULT_OK);
                    break;
                case UI_SHOW_BT_ENABLED:
                    btDevicesActivity.openBT(RESULT_NOT_OK);
                    break;
                case UI_SHOW_OR_HIDE_GPS_ENABLED:
                    btDevicesActivity.openGPS();
                    break;
                case UI_DISMISS_DIALOG:
                    btDevicesActivity.hideDialog();
                    break;
                case UI_GO_SETTINGS:
                    btDevicesActivity.goSettingUI();
                    break;
                case UI_STOP_BT_SCAN:
                    break;
            }
        }
    }

    private boolean isBTOpen(BluetoothAdapter btAdapter) {
        if (btAdapter == null || !btAdapter.isEnabled()) {
            return false;
        }
        return true;
    }

    private IntentFilter getFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(ACTION_REFRESH);
        filter.addAction(BLEService.ACTION_GATT_CONNECTED);
        filter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        return filter;
    }

    public void openBT(int resultCode) {
        if (resultCode == RESULT_OK) {
            isBTOpen = true;
        } else {
            isBTOpen = false;
        }
        if (isBTOpen) {
            mLlBtSwitch.setVisibility(View.GONE);
        } else {
            mLlBtSwitch.setVisibility(View.VISIBLE);
        }
    }

    private void openGPS() {
        isGPSOpen = isGPSOpen();
        if (isGPSOpen) {
            Log.d(TAG,"GPS is open");
            mLlLocationSwitch.setVisibility(View.GONE);
        } else {
            Log.d(TAG,"GPS is not open");
            mLlLocationSwitch.setVisibility(View.VISIBLE);
        }
    }

    private boolean isGPSOpen() {
        LocationManager locationManager = (LocationManager) (App.getAppContext()).getSystemService(Context.LOCATION_SERVICE);
        boolean gpsLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkLocation = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG,"gpsLocation"+gpsLocation);
        Log.d(TAG,"networkLocation"+networkLocation);
        if (gpsLocation || networkLocation) {
            return true;
        }
        return false;
    }

    private void hideDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public void goSettingUI() {
        AppUiUtil.launchSettingUI(App.getAppContext());
    }
}
