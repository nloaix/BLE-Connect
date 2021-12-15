package com.jht.bleconnect.common;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.jht.bleconnect.ui.BleDevicesActivity;

public class DialogUtil {

    public static AlertDialog showMsgAlertDialog(Context context, String msg, final BleDevicesActivity.UIHandler uiHandler){
        AlertDialog alertDialog = new AlertDialog.Builder(context).setCancelable(false)
                .setMessage(msg).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uiHandler.sendEmptyMessage(BleDevicesActivity.UI_DISMISS_DIALOG);
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uiHandler.sendEmptyMessage(BleDevicesActivity.UI_GO_SETTINGS);
                    }
                }).create();
        return alertDialog;

    }
}
