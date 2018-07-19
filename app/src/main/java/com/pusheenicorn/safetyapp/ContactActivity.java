package com.pusheenicorn.safetyapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity{
    BottomNavigationView bottomNavigationView;
    ImageButton btnSendMessage;
    EditText etMessage;
    EditText etPhoneNumber;
    String phonenumber;
    TextView tvFriendsTitle;
    ImageButton btnCall;
    // Define global current user.
    User currentUser;
    static final int MAX_SMS_MESSAGE_LENGTH = 160;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        checkPermissionsPlease();
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(etPhoneNumber.getText().toString(), etMessage.getText().toString());
                Toast.makeText(ContactActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
            }
        });
        etMessage = findViewById(R.id.etMessage);
        btnCall = findViewById(R.id.btnCall);
        tvFriendsTitle = findViewById(R.id.tvFriendsTitle);
        btnCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialContactPhone(etPhoneNumber.getText().toString());
            }
        });
        // Get the current user and cast appropriately.
        currentUser = (User) ParseUser.getCurrentUser();
        phonenumber = currentUser.getPhonNumber();
        etMessage = findViewById(R.id.etMessage);
        etPhoneNumber  = findViewById(R.id.etPhoneNumber);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent goHome = new Intent(ContactActivity.this, MainActivity.class);
                        Toast.makeText(ContactActivity.this, "Success!", Toast.LENGTH_LONG ).show();
                        startActivity(goHome);
                        finish();
                        return true;
                    case R.id.action_message:
//                      //Toast.makeText(ContactActivity.this, "Already on Messages Page!", Toast.LENGTH_LONG ).show();
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent goFriends = new Intent(ContactActivity.this, FriendsActivity.class);
                        Toast.makeText(ContactActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        startActivity(goFriends);
                        finish();
                        return true;
                }
                return true;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.action_message);

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
                    new Intent(this, ContactActivity.class), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, pi, null);
        int length = message.length();

        if(length > MAX_SMS_MESSAGE_LENGTH)
        {
            ArrayList<String> messagelist = sms.divideMessage(message);

            sms.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
        }
        else
        {
            sms.sendTextMessage(phonenumber, null, message, pi, null);
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }


}
