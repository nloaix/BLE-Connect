package com.jht.bleconnect.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.jht.bleconnect.R;

import java.util.ArrayList;
import java.util.List;

public class BTDeviceLVAdapter extends BaseAdapter {
    private String TAG = getClass().getSimpleName();
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private Context context;
    private ArrayList<Integer> mRSSIs = new ArrayList<Integer>();
    private int selectedId;

    public BTDeviceLVAdapter(Context context ){
        this.context = context;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectedId (int position) {
        selectedId = position;
        notifyDataSetInvalidated();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        View inflate = null;
        if (convertView != null){
            inflate = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            inflate = LayoutInflater.from(context).inflate(R.layout.item_lv_bt_device, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.mTvBtName = inflate.findViewById(R.id.tv_bt_name);
            viewHolder.mTvBtMac = inflate.findViewById(R.id.tv_bt_mac);
            viewHolder.mTvBtRssi = inflate.findViewById(R.id.tv_bt_rssi);
            inflate.setTag(viewHolder);
        }

        if (position == selectedId) {
            inflate.setBackgroundColor(Color.GREEN);
        }

        final BluetoothDevice bluetoothDevice = deviceList.get(position);
        int rssi = mRSSIs.get(position);
        String rssiString = (rssi == 0) ? "N/A" : "RSSI:"+rssi+"dBm";
        if (bluetoothDevice.getName() != null){
            viewHolder.mTvBtName.setText(bluetoothDevice.getName());
            viewHolder.mTvBtRssi.setText(rssiString);
        }
        viewHolder.mTvBtMac.setText(bluetoothDevice.getAddress());

        return inflate;
    }

    public void addDevice(BluetoothDevice device,int rssi){
        boolean canAdd = true;
        for (BluetoothDevice device1 : deviceList){
            if (device1.getAddress().equals(device.getAddress())){
                canAdd = false;
            }
        }
        if (canAdd){
            mRSSIs.add(rssi);
            deviceList.add(device);
            notifyDataSetChanged();
        }
    }

    public void clearDevicesList(){
        mRSSIs.clear();
        deviceList.clear();
        notifyDataSetChanged();
    }


    class ViewHolder{
        TextView mTvBtName;
        TextView mTvBtMac;
        TextView mTvBtRssi;
    }

}
