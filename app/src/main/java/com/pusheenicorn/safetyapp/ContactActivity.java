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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;

public class ContactActivity extends BaseActivity {
    //declared variables
    BottomNavigationView bottomNavigationView; //bottom navigation view that allows user to toggle between screens
    ImageButton btnSendMessage; //button that sends the sms message
    EditText etMessage; //text field that can be edited to include user's message
    EditText etPhoneNumber; //text field for the phone number that the user wishes to message or call
    String phonenumber; //phone number that should be contacted
    Button btnChat; //button that leads to the chat activity
    TextView tvFriendsTitle; //title to display name of current activity
    ImageButton btnCall; //button that allows user to call given phone number
    // Define global current user.
    User currentUser;
    static final int MAX_SMS_MESSAGE_LENGTH = 160; //max length for a message before it is split

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //check for permissions to allow user to call or message
        checkPermissionsPlease();
        //initialize the chat button and intent to take user to the chat activity
        btnChat = findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goChat = new Intent(ContactActivity.this, ChatActivity.class);
                startActivity(goChat);
            }
        });
        //initialize the send button and intent to send a message
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS(etPhoneNumber.getText().toString(), etMessage.getText().toString());
                Toast.makeText(ContactActivity.this, "Message sent!", Toast.LENGTH_LONG).show();
            }
        });
        etMessage = findViewById(R.id.etMessage);
        tvFriendsTitle = findViewById(R.id.tvFriendsTitle);
        //initialize the call button and method to dial a phone number
        btnCall = findViewById(R.id.btnCall);
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
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        //initialize the bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_contact);
        setNavigationDestinations(ContactActivity.this, bottomNavigationView);

        initializeNavItems(mNavItems);
        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position, mDrawerList, mDrawerPane, mDrawerLayout,
                        ContactActivity.this, mNavItems);
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
                new Intent(this, ContactActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        int length = message.length();
        //check to see if the length is above the maximum character length, and if so, to divide message
        if (length > MAX_SMS_MESSAGE_LENGTH) {
            ArrayList<String> messagelist = sms.divideMessage(message);
            sms.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
        } else {
            sms.sendTextMessage(phonenumber, null, message, pi, null);
        }
    }

    //method for dialing the contact number provided
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }


    public void onSettings(View view) {
        Intent intent = new Intent(ContactActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
