package com.pusheenicorn.safetyapp.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.pusheenicorn.safetyapp.AlarmController;
import com.pusheenicorn.safetyapp.MessagesReceiver;

public class AlarmStopReceiver extends WakefulBroadcastReceiver{
    Context mContext;
    public static final int SERVICE_ID = 20;
    AlarmController alarmController;


    // Called automatically when the Broadcast CheckinReceiver is triggered.
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

            alarmController = MessagesReceiver.ALARM_CONTROLELR;
            alarmController.stopSound();
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
    }
}
