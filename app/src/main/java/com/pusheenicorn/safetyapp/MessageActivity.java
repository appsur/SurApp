package com.pusheenicorn.safetyapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MessageActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        checkPermissionsPlease();
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent goHome = new Intent(MessageActivity.this, MainActivity.class);
                        Toast.makeText(MessageActivity.this, "Success!", Toast.LENGTH_LONG ).show();
                        startActivity(goHome);
                        return true;
                    case R.id.action_message:
                        Toast.makeText(MessageActivity.this, "Already on Messages Page!", Toast.LENGTH_LONG ).show();
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent goFriends = new Intent(MessageActivity.this, FriendsActivity.class);
                        Toast.makeText(MessageActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        return true;
                }
                return true;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.action_message);
//        sendSMS("6304862146", "test!!");

    }

    private void checkPermissionsPlease() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
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
