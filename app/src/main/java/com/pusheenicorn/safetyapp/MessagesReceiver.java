package com.pusheenicorn.safetyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;

import static com.parse.Parse.getApplicationContext;

public class MessagesReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //get message passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String str = "";
        String messageBody = "";

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus != null ? pdus.length : 0];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) (pdus != null ? pdus[i] : null));
                messageBody += messages[i].getMessageBody();
                if (messageBody.equals("SUR")) {
                    AlarmController alarmController = new AlarmController(context);
                    alarmController.playSound();
//                    InputStream inputStream  = context.getResources().openRawResource(R.drawable);
//                    DataInputStream dataInputStream = new DataInputStream(inputStream);
//                    NotificationCompat.Builder mBuilder =
//                            new NotificationCompat.Builder(context);
//                    mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    r.play();
                    NotificationUtil notificationUtil = new NotificationUtil(context, alarmController);
                    notificationUtil.scheduleNotification(notificationUtil.getAlarmNotification(), 0);
                }
                str += messages[i].getOriginatingAddress();
                str += ": ";
                str += messages[i].getMessageBody();
                str += "\n";
            }
            //display message
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

            //send a broadcast intent to update the Sms received in a TextView
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("message", str);
            broadcastIntent.putExtra("body", messageBody);
            context.sendBroadcast(broadcastIntent);
        }
    }
}
