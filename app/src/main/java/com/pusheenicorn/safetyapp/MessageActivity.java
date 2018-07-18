package com.pusheenicorn.safetyapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        sendSMS("6304862146", "test!!");
    }
    private void sendSMS(String phoneNumber, String message)
    {
        Log.v("phoneNumber",phoneNumber);
        Log.v("Message",message);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this,MessageActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }
}
