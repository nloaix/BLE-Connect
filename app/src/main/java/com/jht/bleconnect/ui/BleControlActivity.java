package com.jht.bleconnect.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jht.bleconnect.App;
import com.jht.bleconnect.R;
import com.jht.bleconnect.adapter.BTDeviceLVAdapter;
import com.jht.bleconnect.adapter.ExpandableLvBTAdapter;
import com.jht.bleconnect.entity.FitnessMachineFeature;
import com.jht.bleconnect.service.BLEService;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import static com.jht.bleconnect.ui.BleDevicesActivity.ACTION_REFRESH;

public class BleControlActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private RelativeLayout mRlAllBtInfo;
    private TextView mTvDeviceMac;
    private TextView mTvDeviceData;
    private ExpandableListView mElvBtServices;

    private BluetoothDevice device;
    private BLEService bleService;
    private ExpandableLvBTAdapter expandableLvBTAdapter;
    private ControlActivityUIHandler uiHandler;
    private BTDeviceLVAdapter btDeviceLVAdapter;

    private boolean isWrite;
    private TextView connect_result;


    // 16进制转换
    public static String bytes2HexString(byte[] array) {
        StringBuilder builder = new StringBuilder();
        for (byte b : array) {
            String hex = Integer.toHexString(b & 0xFF);

            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            builder.append(hex);
        }

        return builder.toString().toUpperCase();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BLEService.BleBinder) service).getBleService();
            Log.d(TAG,"onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bleService = null;
            Log.d(TAG,"onServiceDisConnected");
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG,"NOW IS CONNECTED");
            } else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG,"NOW IS DISCONNECTED");
            } else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(bleService.getCurrentGattServices());
                byte[] value = {
//                        (byte) 0xFE, 0x05, 0x04, 0x01, 0x01, 0x01, 0x0A,0x14
//                        (byte) 0xFE, 0x13, 0x01, 0x03, 0x15
//                        吮吸器震动3挡
                        (byte) 0xFE,0x11,0x01,0x23,0x33
                };
                isWrite = bleService.writeRXCharacteristic(value);
                bleService.enableTXNotification();
            } else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                if("flag".equals(intent.getStringExtra("FitnessMachineFeature"))){
                    Log.d(TAG,"is ACTION_DATA_AVAILABLE");
//                    displayFitnessMachineFeatureData(Html.fromHtml(intent.getStringExtra(BLEService.EXTRA_DATA)));
                }else {
                    final byte[] rxValue = intent.getByteArrayExtra (BLEService.EXTRA_DATA);
                    Log.d(TAG,"此处的rxvalue======" + bytes2HexString(rxValue));
//                    displayData(Html.fromHtml(intent.getStringExtra(BLEService.EXTRA_DATA)));
                    if (isWrite) {
                        connect_result.setText("连接结果：成功");
                        connect_result.setTextColor(0xff00bc12);
                    } else {
                        connect_result.setText("连接结果：失败");
                        connect_result.setTextColor(0xfff80000);

                    }
                }

            }
        }
    };

    private List<BluetoothGattService> currentGattServices = null;
    private HashMap<BluetoothGattService, List<BluetoothGattCharacteristic>> currentServiceAndCharacteristic = null;

    private void displayGattServices(List<BluetoothGattService> currentGattServices) {
        this.currentGattServices = currentGattServices;
        currentServiceAndCharacteristic = new HashMap<>();
        for (BluetoothGattService  bluetoothGattService : currentGattServices){
            List<BluetoothGattCharacteristic> listOfCharacteristics = bluetoothGattService.getCharacteristics();
            currentServiceAndCharacteristic.put(bluetoothGattService,listOfCharacteristics);
            expandableLvBTAdapter.addService(bluetoothGattService);
        }
        for(int i = 0; i<currentGattServices.size();i++ ){
            mElvBtServices.expandGroup(i);
            mElvBtServices.collapseGroup(i);
        }
    }

    private void displayData(Spanned stringExtra) {
        mTvDeviceData.setText(stringExtra);
    }

    private void displayFitnessMachineFeatureData(Spanned stringExtra){
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_item_show_fmf_data, null);
        TextView tv_fmf_data = inflate.findViewById(R.id.tv_fmf_data);
        Button btn_close = inflate.findViewById(R.id.btn_close_dialog);
        tv_fmf_data.setText(stringExtra);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).setView(inflate).create();
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_control);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null){
            supportActionBar.hide();
        }
        Intent intent = getIntent();
        device = (BluetoothDevice)intent.getParcelableExtra("device");
        initViews();
        Intent bleServiceIntent = new Intent(this, BLEService.class);
        bleServiceIntent.putExtra("device",device);
        bindService(bleServiceIntent,serviceConnection,BIND_AUTO_CREATE);
        connect_result = (TextView) findViewById(R.id.connect_result);
    }

    private void initViews() {
        mRlAllBtInfo = findViewById(R.id.rl_all_bt_info);
        mRlAllBtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View inflate = LayoutInflater.from(BleControlActivity.this).inflate(R.layout.dialog_item_show_fmf_data, null);
                TextView tv_fmf_data = inflate.findViewById(R.id.tv_fmf_data);
                Button btn_close = inflate.findViewById(R.id.btn_close_dialog);
                String opcode = " Fitness Machine Control Point op code : \n"
                        + "Op Code : Definition : Parameter"
                        + "\n 0x00 : Request Control : N/A"
                        + "\n 0x01 : Reset : N/A"
                        + "\n 0x02 : Set Target Speed : UINT16"
                        + "\n 0x03 : Set Inclination  : SINT16"
                        + "\n 0x04 : Set Resistance Level : UINT8"
                        + "\n 0x05 : Set Target Power : SINT16"
                        + "\n 0x06 : Set Heart Rate : UINT8"
                        + "\n 0x07 : Start or Resume : N/A"
                        + "\n 0x08 : Stop or Pause : stop-01,pause-02"
                        + "\n 0x09 : Set Expended Energy : UINT16"
                        + "\n 0x0a : Set Number of Steps : UINT16"
                        + "\n 0x0b : Set Number of Strides : UINT16"
                        + "\n 0x0c : Set Distance :UINT24"
                        + "\n 0x0d : Set Training Time : UINT16"
                        + "\n 0x0e : Set Two HeartRate Zones : Array"
                        + "\n 0x0f : Set Three HeartRate Zones: Array"
                        + "\n 0x10 : Set Five HeartRate Zones : Array"
                        + "\n 0x11 : Set IndoorBike Simulation: Array"
                        + "\n 0x12 : Set Wheel Circumference: UINT16"
                        + "\n 0x13 : Spin Down Control : start-01,ignore-02 "
                        + "\n 0x14 : Set Cadence : UINT16"
                        ;
                tv_fmf_data.setText(opcode);
                final AlertDialog alertDialog = new AlertDialog.Builder(BleControlActivity.this).setView(inflate).create();
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });
        mTvDeviceMac = findViewById(R.id.tv_device_mac);
        mTvDeviceMac.setText(device.getName());
        mTvDeviceData = findViewById(R.id.tv_device_data);
        mTvDeviceData.setText("");
        mElvBtServices = findViewById(R.id.elv_bt_services);
        uiHandler = new ControlActivityUIHandler(this);
        expandableLvBTAdapter = new ExpandableLvBTAdapter(App.getAppContext(),uiHandler);
        mElvBtServices.setAdapter(expandableLvBTAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver,getFilters());
        Log.d(TAG,"bt is resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
        Log.d(TAG,"bt is pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"bt is onDestroy");
        unbindService(serviceConnection);
        bleService.disconnect();  // 注销此行 表示GATT服务没有断开 重新搜索失败 重新开关机设备后才可以搜到
    }

    private static IntentFilter getFilters() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    public static final int HANDLER_CODE_READ_DATA = 1;
    public static final int HANDLER_CODE_NOTIFY = 2;
    public static final int HANDLER_CODE_SEND_DATA = 3;

    public static class ControlActivityUIHandler extends Handler {
        private WeakReference<BleControlActivity> ref = null;

        public ControlActivityUIHandler(BleControlActivity bleControlActivity) {
            ref = new WeakReference<BleControlActivity>(bleControlActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            BleControlActivity bleControlActivity = ref.get();
            Bundle data = msg.getData();
            String characteristic_uuid = data.getString("characteristic_uuid");
            switch (msg.what){
                case HANDLER_CODE_READ_DATA:
                    bleControlActivity.readCharacteristic(characteristic_uuid);
                    break;
                case HANDLER_CODE_NOTIFY:
                    bleControlActivity.setCharacteristicNotify(characteristic_uuid);
                    break;
                case HANDLER_CODE_SEND_DATA:
                    bleControlActivity.writeCharacteristic(characteristic_uuid);
                    break;
            }
        }
    }
    private AlertDialog alertDialog ;
    private void writeCharacteristic(String characteristic_uuid) {
        View inflate = LayoutInflater.from(App.getAppContext()).inflate(R.layout.dialog_view_write_bt_data, null);
        final String char_uuid = characteristic_uuid;
        final EditText et_operation_code = inflate.findViewById(R.id.et_operation_code);
        final EditText et_prams = inflate.findViewById(R.id.et_bt_prams);
        Button btn_send_data = inflate.findViewById(R.id.btn_send);
        btn_send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: parms == '' ? " + ("".equals(et_prams.getText().toString())));
                bleService.writeCharacteristic(char_uuid,et_operation_code.getText().toString(),et_prams.getText().toString());
                if (alertDialog != null){
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog = new AlertDialog.Builder(this).setView(inflate).show();
    }

    private void setCharacteristicNotify(String characteristic_uuid) {
        bleService.setCharacteristicNotification(characteristic_uuid,true);
    }

    private void readCharacteristic(String characteristic_uuid) {
        bleService.readCharacteristic(characteristic_uuid);
    }
}
