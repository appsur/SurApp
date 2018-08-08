package com.pusheenicorn.safetyapp.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pusheenicorn.safetyapp.AlarmController;
import com.pusheenicorn.safetyapp.MessagesReceiver;
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

            alarmController = MessagesReceiver.alarmController;
            alarmController.stopSound();
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
    }
}
