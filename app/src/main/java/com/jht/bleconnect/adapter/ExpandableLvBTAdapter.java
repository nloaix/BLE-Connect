package com.jht.bleconnect.adapter;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.jht.bleconnect.App;
import com.jht.bleconnect.R;
import com.jht.bleconnect.common.AttributeLookup;
import com.jht.bleconnect.common.BaseUtils;
import com.jht.bleconnect.ui.BleControlActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.jht.bleconnect.ui.BleControlActivity.HANDLER_CODE_NOTIFY;
import static com.jht.bleconnect.ui.BleControlActivity.HANDLER_CODE_READ_DATA;
import static com.jht.bleconnect.ui.BleControlActivity.HANDLER_CODE_SEND_DATA;

public class ExpandableLvBTAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "ExpandableLvBTAdapter";
    private Context context;
    private List<BluetoothGattService> servicesGroup = new ArrayList<>();
    private List<BluetoothGattCharacteristic> characteristicsChild = new ArrayList<>();
    private HashMap<String, List<BluetoothGattCharacteristic>> map = new HashMap<>();
    private AttributeLookup attributeLookup = null;
    private BleControlActivity.ControlActivityUIHandler controlActivityUIHandler;

    public ExpandableLvBTAdapter(Context context, BleControlActivity.ControlActivityUIHandler uiHandler) {
        this.context = context;
        attributeLookup = new AttributeLookup(context);
        this.controlActivityUIHandler = uiHandler;
    }

    @Override
    public int getGroupCount() {
        return servicesGroup.size();
    }

    @Override
    public int getChildrenCount(int groupIndex) {
        String uuidStr = servicesGroup.get(groupIndex).getUuid().toString();
        return map.get(uuidStr).size();
    }

    @Override
    public Object getGroup(int groupIndex) {
        return servicesGroup.get(groupIndex);
    }

    @Override
    public Object getChild(int groupIndex, int childIndex) {
        String uuidStr = servicesGroup.get(groupIndex).getUuid().toString();
        return map.get(uuidStr).get(childIndex);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void addCharacteristic(int position, String uuidStr, List<BluetoothGattCharacteristic> bluetoothGattCharacteristics) {
        characteristicsChild = new ArrayList<>();
        for (BluetoothGattCharacteristic btCharacteristic : bluetoothGattCharacteristics) {
            characteristicsChild.add(btCharacteristic);
        }
        map.put(uuidStr, characteristicsChild);
        notifyDataSetChanged();
    }

    public void addService(BluetoothGattService service) {
        servicesGroup.add(service);
        int size = servicesGroup.size() - 1;
        addCharacteristic(size, service.getUuid().toString(), service.getCharacteristics());
    }

    public void clearAllList() {
        servicesGroup.clear();
        characteristicsChild.clear();
        map.clear();
        notifyDataSetChanged();
    }

    public void addBTServices(List<BluetoothGattService> services) {
        for (BluetoothGattService gattService : services) {
            addService(gattService);
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elv_group_service, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mTvBtServiceName = convertView.findViewById(R.id.tv_bt_service_name);
            groupViewHolder.mTvBtServiceUuid = convertView.findViewById(R.id.tv_bt_service_uuid);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        UUID uuid = servicesGroup.get(groupPosition).getUuid();
        groupViewHolder.mTvBtServiceName.setText(attributeLookup.getService(uuid));
        groupViewHolder.mTvBtServiceUuid.setText(uuid.toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elv_child_characteristic, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.mLlBtCharacteristicInfo = convertView.findViewById(R.id.ll_bt_characteristic_info);
            childViewHolder.mTvBtCharacteristicName = convertView.findViewById(R.id.tv_bt_characteristic_name);
            childViewHolder.mTvBtCharacteristicUuid = convertView.findViewById(R.id.tv_bt_characteristic_uuid);
            childViewHolder.mTvBtCharacteristicProperties = convertView.findViewById(R.id.tv_bt_characteristic_properties);
            childViewHolder.mBtnReadData = convertView.findViewById(R.id.btn_read_data);
            childViewHolder.mBtnNotify = convertView.findViewById(R.id.btn_notify);
            childViewHolder.mBtnSendData = convertView.findViewById(R.id.btn_send_data);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        String uuidStr = servicesGroup.get(groupPosition).getUuid().toString();
        BluetoothGattCharacteristic bluetoothGattCharacteristic = map.get(uuidStr).get(childPosition);
        final UUID characteristic_uuid = bluetoothGattCharacteristic.getUuid();
        childViewHolder.mTvBtCharacteristicName.setText(attributeLookup.getCharacteristic(characteristic_uuid));
        childViewHolder.mTvBtCharacteristicUuid.setText(characteristic_uuid.toString());
        int properties = bluetoothGattCharacteristic.getProperties();
        String propertiesStr = getCharacterProperties(properties);
        childViewHolder.mTvBtCharacteristicProperties.setText(propertiesStr);

        childViewHolder.mBtnReadData.setVisibility(View.GONE);
        childViewHolder.mBtnNotify.setVisibility(View.GONE);
        childViewHolder.mBtnSendData.setVisibility(View.GONE);
        if (propertiesStr.contains("READ")) {
            childViewHolder.mBtnReadData.setVisibility(View.VISIBLE);
            childViewHolder.mBtnReadData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = controlActivityUIHandler.obtainMessage();
                    Bundle data = new Bundle();
                    data.putString("characteristic_uuid", characteristic_uuid.toString());
                    message.setData(data);
                    message.what = HANDLER_CODE_READ_DATA;
                    controlActivityUIHandler.sendMessage(message);
                }
            });
        }
        if (propertiesStr.contains("NOTIFY")) {
            int visibility = childViewHolder.mBtnReadData.getVisibility();
            childViewHolder.mBtnNotify.setVisibility(View.VISIBLE);
            childViewHolder.mBtnNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = controlActivityUIHandler.obtainMessage();
                    Bundle data = new Bundle();
                    data.putString("characteristic_uuid", characteristic_uuid.toString());
                    Log.i(TAG, "onClick: uuid ==> " + characteristic_uuid.toString());
                    message.setData(data);
                    message.what = HANDLER_CODE_NOTIFY;
                    controlActivityUIHandler.sendMessage(message);
                }
            });
            if (visibility == View.VISIBLE){
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)childViewHolder.mBtnNotify.getLayoutParams();
                layoutParams.setMargins(BaseUtils.dip2px(App.getAppContext(),20),0,0,0);
                childViewHolder.mBtnNotify.setLayoutParams(layoutParams);
            }
        }
        if (propertiesStr.contains("WRITE")) {
            int visibility = childViewHolder.mBtnNotify.getVisibility();
            int visibility1 = childViewHolder.mBtnReadData.getVisibility();
            childViewHolder.mBtnSendData.setVisibility(View.VISIBLE);
            childViewHolder.mBtnSendData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = controlActivityUIHandler.obtainMessage();
                    Bundle data = new Bundle();
                    data.putString("characteristic_uuid", characteristic_uuid.toString());
                    message.setData(data);
                    message.what = HANDLER_CODE_SEND_DATA;
                    controlActivityUIHandler.sendMessage(message);
                }
            });
            if (visibility == View.VISIBLE || visibility1 == View.VISIBLE){
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)childViewHolder.mBtnSendData.getLayoutParams();
                layoutParams.setMargins(BaseUtils.dip2px(App.getAppContext(),20),0,0,0);
                childViewHolder.mBtnSendData.setLayoutParams(layoutParams);
            } else {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)childViewHolder.mBtnSendData.getLayoutParams();
                layoutParams.setMargins(BaseUtils.dip2px(App.getAppContext(),0),0,0,0);
                childViewHolder.mBtnSendData.setLayoutParams(layoutParams);
            }
        }
        return convertView;
    }

    public String getCharacterProperties(int properties) {
        String propertiesStr = "";
        if ((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) == BluetoothGattCharacteristic.PROPERTY_BROADCAST) {
            propertiesStr += "BROADCAST,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_READ) == BluetoothGattCharacteristic.PROPERTY_READ ) {
            propertiesStr += "READ,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) {
            propertiesStr += "WRITE_NO_RESPONSE,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) == BluetoothGattCharacteristic.PROPERTY_WRITE) {
            propertiesStr += "WRITE,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
            propertiesStr += "NOTIFY,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) == BluetoothGattCharacteristic.PROPERTY_INDICATE) {
            propertiesStr += "INDICATE,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) == BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) {
            propertiesStr += "SIGNED_WRITE,";
        }
        if ((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) == BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) {
            propertiesStr += "EXTENDED_PROPS,";
        }
        return propertiesStr;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }


    static class GroupViewHolder {
        TextView mTvBtServiceName;
        TextView mTvBtServiceUuid;
    }


    static class ChildViewHolder {
        TextView mTvBtCharacteristicName;
        TextView mTvBtCharacteristicUuid;
        TextView mTvBtCharacteristicProperties;
        Button mBtnNotify;
        Button mBtnSendData;
        LinearLayout mLlBtCharacteristicInfo;
        Button mBtnReadData;
    }
}
