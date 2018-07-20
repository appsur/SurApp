package com.pusheenicorn.safetyapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    Button btnSendText;
    EditText etTextMessage;
    IntentFilter intentFilter;
    TextView inTxt;

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //display message in the text view
            inTxt = (TextView) findViewById(R.id.tvTextMessage);
            inTxt.setText(intent.getExtras().getString("message"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        btnSendText = (Button) findViewById(R.id.btnSendText);
        etTextMessage = (EditText) findViewById(R.id.etTextMessage);
        btnSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etTextMessage.getText().toString();
                String number = "6304862146";
                sendMessage(number, message);
            }
        });
    }
    protected void sendMessage (String number, String message) {
            String SENT = "Message Sent!!";
            String DELIVERED = "Message Delivered!";

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
            PendingIntent delieveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
            SmsManager sms= SmsManager.getDefault();
            sms.sendTextMessage(number, null, message, sentPI, delieveredPI);
        Toast.makeText(ChatActivity.this, "YOU SENT A MESSAGE!!", Toast.LENGTH_SHORT).show();
        }

        protected void onResume() {
        //register the receiver
            registerReceiver(intentReceiver, intentFilter);
            super.onResume();
        }

        protected void onPause() {
        //unregister the receiver
            unregisterReceiver(intentReceiver);
            super.onPause();
        }

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String address = extras.getString("address");
//            String message = extras.getString("message");
//            TextView addressField = (TextView) findViewById(R.id.address);
//            TextView messageField = (TextView) findViewById(R.id.message);
//            addressField.setText(address);
//            messageField.setText(message);
//        }
}
