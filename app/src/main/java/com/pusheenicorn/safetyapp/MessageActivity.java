package com.pusheenicorn.safetyapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.User;

public class MessageActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;
    Button btnSendMessage;
    EditText etMessage;
    EditText etPhoneNumber;
    String phonenumber;
    // Define global current user.
    User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        checkPermissionsPlease();
        btnSendMessage = findViewById(R.id.btnSendMessage);
        etMessage = findViewById(R.id.etMessage);
        // Get the current user and cast appropriately.
        currentUser = (User) ParseUser.getCurrentUser();
        phonenumber = currentUser.getPhonNumber();
        etMessage = findViewById(R.id.etMessage);
        etPhoneNumber  = findViewById(R.id.tvPhoneNumber);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent goHome = new Intent(MessageActivity.this, MainActivity.class);
                        Toast.makeText(MessageActivity.this, "Success!", Toast.LENGTH_LONG ).show();
                        startActivity(goHome);
                        finish();
                        return true;
                    case R.id.action_message:
//                      //Toast.makeText(MessageActivity.this, "Already on Messages Page!", Toast.LENGTH_LONG ).show();
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent goFriends = new Intent(MessageActivity.this, FriendsActivity.class);
                        Toast.makeText(MessageActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        startActivity(goFriends);
                        finish();
                        return true;
                }
                return true;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.action_message);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(etPhoneNumber.getText().toString(), etMessage.getText().toString());
                Toast.makeText(MessageActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void checkPermissionsPlease() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
    }
    private void sendSMS(String phoneNumber, String message) {
            Log.v("phoneNumber", phoneNumber);
            Log.v("Message", message);
            PendingIntent pi = PendingIntent.getActivity(this, 0,
                    new Intent(this, MessageActivity.class), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


}
