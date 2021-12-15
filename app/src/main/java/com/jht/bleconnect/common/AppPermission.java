package com.jht.bleconnect.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppPermission {
    private static String TAG = "AppPermission";

    public static String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static List<String> list = null;

    public static String[] getAppPermissionsToArray(){
        return permissions;
    }

    public static List<String> getAppPermissionsToList(){
        if (list == null){
            list = new ArrayList<>();
            Collections.addAll(list,permissions);
        }
        return list;
    }

    public static boolean requestAppPermissions(Activity activity,int REQUEST_PERMISSIONS){
        String[] appPermissions = getAppPermissionsToArray();
        List<String> appPermissionsToList = new ArrayList<>();
        for (int i = 0; i < appPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, appPermissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "requestAppPermissions: " + appPermissions[i]);
                appPermissionsToList.add(appPermissions[i]);
            }
        }
        if (appPermissionsToList.size() != 0) {
            String[] requestPermissions = new String[appPermissionsToList.size()];
            for (int i = 0; i < appPermissionsToList.size(); i++) {
                Log.i(TAG, "request Permissions: ==> " + appPermissionsToList.get(i));
                requestPermissions[i] = appPermissionsToList.get(i);
            }
            activity.requestPermissions(requestPermissions,REQUEST_PERMISSIONS);
            return false;
        }else {
            return true;
        }
    }
}
