package com.jht.bleconnect.common;

import android.content.Context;

import com.jht.bleconnect.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AttributeLookup {
    private Map<UUID, String> mUuidMap;
    private Context mContext = null;
    private Map<String,UUID> mUUID;
    public AttributeLookup(Context context) {
        this.mContext = context;

        mUuidMap = new HashMap<UUID, String>();
        mUUID = new HashMap();
        String[] uuidStr = context.getResources().getStringArray(R.array.uuids);
        for (int i = 0; i < uuidStr.length; i++) {
            String[] parts = uuidStr[i].split("\\|");
            if (parts.length == 2){
                mUuidMap.put(UUID.fromString(parts[0]),parts[1]);
                mUUID.put(parts[1],UUID.fromString(parts[0]));
            }
        }
    }

    public String getService(UUID uuid) {
        if (mUuidMap.containsKey(uuid)) return mUuidMap.get(uuid);
        return mContext.getString(R.string.unknown_service);
    }

    public String getCharacteristic(UUID uuid) {
        if (mUuidMap.containsKey(uuid)) return mUuidMap.get(uuid);
        return mContext.getString(R.string.unknown_char);
    }

    public String getDescriptor(UUID uuid){
        if (mUuidMap.containsKey(uuid)) return mUuidMap.get(uuid);
        return mContext.getString(R.string.unknown_char);
    }

    public UUID getCharacteristicUUID(String characteristicName){
        if (mUUID.containsKey(characteristicName)) return mUUID.get(characteristicName);
        return null;
    }
}
