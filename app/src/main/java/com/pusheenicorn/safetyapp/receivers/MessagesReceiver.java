package com.pusheenicorn.safetyapp.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.AlarmController;
import com.pusheenicorn.safetyapp.models.Keyword;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.util.List;

public class MessagesReceiver extends WakefulBroadcastReceiver {
    //declare a static alarm controller that will be used to start and stop ringing a friend
    public static AlarmController ALARM_CONTROLELR;
    //declare the unique idea for the notification that is sent when the user receives a trigger word
    public static final int ID = 2027;
    //declare class variables
    User mCurrentUser;
    Keyword messageComp;
    String messageBody = "";

    @Override
    public void onReceive(final Context context, Intent intent) {
        //get message passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages;
        String str = "";
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
            //display message on the screen for users to see
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            //create a keyword query to see if a random trigger code has been sent to the user through the ring friend feature
            final Keyword.Query keywordQuery = new Keyword.Query();
            keywordQuery.findInBackground(new FindCallback<Keyword>() {
                @Override
                public void done(List<Keyword> objects, ParseException e) {
                    if (e == null) {
                        if (objects != null) {
                            messageComp = objects.get(objects.size() - 1);
                            String keyword = messageComp.getKeyword();
                            //check to see if the body of the message has the most recently created keyword
                            if (messageBody.equals(keyword)) {
                                //create an alarm controller that will play a sound
                                ALARM_CONTROLELR = new AlarmController(context);
                                ALARM_CONTROLELR.playSound();
                                //create a notification that allows the user to stop the alarm from the push notification button
                                NotificationUtil notificationUtil = new NotificationUtil(context);
                                notificationUtil.scheduleNotification(
                                        notificationUtil.getAlarmNotification(), ID,0);
                            }
                        }
                    }
                }
            });

            //send a broadcast intent to update the Sms received in a TextView
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("SMS_RECEIVED_ACTION");
            broadcastIntent.putExtra("message", str);
            broadcastIntent.putExtra("body", messageBody);
            context.sendBroadcast(broadcastIntent);
        }
    }
}
