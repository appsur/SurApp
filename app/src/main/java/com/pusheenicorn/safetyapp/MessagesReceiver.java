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

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

public class MessagesReceiver extends WakefulBroadcastReceiver {
    User mCurrentUser;
    @Override
    public void onReceive(Context context, Intent intent) {
        //get message passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String str = "";
        String messageBody = "";
        mCurrentUser = (User) ParseUser.getCurrentUser();

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            messages = new SmsMessage[pdus != null ? pdus.length : 0];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) (pdus != null ? pdus[i] : null));
                messageBody += messages[i].getMessageBody();
                str += messages[i].getOriginatingAddress();
                str += ": ";
                str += messages[i].getMessageBody();
                str += "\n";
            }
//            String gettingKeyword = intent.getAction();
//            if (gettingKeyword.equals("my.action.string")) {
//                Toast.makeText(context, intent.getExtras().getString("keyword"), Toast.LENGTH_SHORT).show();
////                //TODO MESSAGEBODY IS NULL
//                if (messageBody == null) {
//                    Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
//                }
//                if (messageBody.equals(intent.getExtras().getString("keyword"))) {

//                    Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
            //give friend a keyword that can be updated through parse
//                String key = intent.getExtras().getString("keyword");
//                }
            Toast.makeText(context, messageBody, Toast.LENGTH_SHORT).show();
                if (messageBody.equals(mCurrentUser.getKeyword())) {
                    AlarmController alarmController = new AlarmController(context);
                    alarmController.playSound();
                    NotificationUtil notificationUtil = new NotificationUtil(context, alarmController);
                    notificationUtil.scheduleNotification(notificationUtil.getAlarmNotification(), 0);
                    Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                }
//            }
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
