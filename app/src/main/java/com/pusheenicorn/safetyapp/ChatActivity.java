package com.pusheenicorn.safetyapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    //declare necessary variables for fields on the screen
    Button btnSendText;
    EditText etTextMessage;
    IntentFilter intentFilter;
    TextView tvTextMessage;

    //initializing variables to populate the friend recycler view
    FriendsAdapter friendAdapter;
    ArrayList<Friend> friends;
    RecyclerView rvFriendList;

    //initializing the current user
    User currentUser;

    //created a broadcast receiver to receive sms messages by responding to system-wide broadcast announcements
    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //display message in the text view
            tvTextMessage = (TextView) findViewById(R.id.tvTextMessage);
            if (intent.equals("SMS_RECEIVED_ACTION")) {
                //set text view with the message and phone number from the reply
                tvTextMessage.setText(intent.getExtras().getString("message"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //intent filter allows activity to know what the broadcast receiver can respond to
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
        //declare fields
        btnSendText = (Button) findViewById(R.id.btnSendText);
        etTextMessage = (EditText) findViewById(R.id.etTextMessage);
        btnSendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //code for sending the message
                String message = etTextMessage.getText().toString();
                //TODO - allow clicking on the friend object to take user to chat activity and auto-populate phone number
                String number = currentUser.getPhonNumber().toString();
                sendMessage(number, message);
            }
        });

        rvFriendList = (RecyclerView) findViewById(R.id.rvFriendList);
        friends = new ArrayList<Friend>();
        // construct the adapter from this data source
        friendAdapter = new FriendsAdapter(friends);
        // recycler view setup
        rvFriendList.setLayoutManager(new LinearLayoutManager(this));
        rvFriendList.setAdapter(friendAdapter);

        //initialize current user
        currentUser = (User) ParseUser.getCurrentUser();
        //populate the friends list
        populateFriendList();

    }

    public void populateFriendList() {
        // Populate the friends list.
        final Friend.Query friendQuery = new Friend.Query();
        friendQuery.getTop().withUser();

        friendQuery.findInBackground(new FindCallback<Friend>() {
            @Override
            public void done(List<Friend> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i > -1; i--)
                    {
                        if (currentUser.getFriends() != null &&
                                currentUser.getFriendIds().contains(objects.get(i).getObjectId()))
                        {
                            // add the friend object
                            friends.add(objects.get(i));
                            // notify the adapter
                            friendAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void sendMessage(String number, String message) {
        String SENT = "Message Sent!!";
        String DELIVERED = "Message Delivered!";

        //token given to allow foreign application to access permissions and execute code
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent delieveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, sentPI, delieveredPI);
        //toast if the text is successfully made
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
}
