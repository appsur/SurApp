package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pusheenicorn.safetyapp.utils.NotificationUtil;

public class AlarmStopReceiver extends WakefulBroadcastReceiver{
    Context mContext;
    public static final String PACKAGE_NAME = "com.pusheenicorn.safetyapp";
    public static final String MAIN_CLASS_NAME = "com.pusheenicorn.safetyapp.MainActivity";
    public static final String ACTION_STOP = "stop";
    public static final int SERVICE_ID = 20;
    public static final String APP_NAME = "Sûr";
    private NotificationUtil notificationUtil;
    AlarmController alarmController;


    // Called automatically when the Broadcast CheckinReceiver is triggered.
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        alarmController = (AlarmController) intent.getParcelableExtra("alarm");
        alarmController.stopSound();
        alarmController.releasePlayer();
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
