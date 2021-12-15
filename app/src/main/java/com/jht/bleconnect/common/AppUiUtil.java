package com.jht.bleconnect.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;


public class AppUiUtil {
    private static String TAG = "AppUtil";

    public static void launchSettingUI(Context context){
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }
}
