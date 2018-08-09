package com.pusheenicorn.safetyapp.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.adapters.ChatAdapter;
import com.pusheenicorn.safetyapp.adapters.friends.FriendsAdapter;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;

public class ContactActivity extends BaseActivity {
    //declared bottom navigation view
    BottomNavigationView bottomNavigationView; //bottom navigation view that allows user to toggle between screens
    //declared views
    ImageButton btnSendMessage; //button that sends the sms message
    EditText etMessage; //text field that can be edited to include user's message
    EditText etPhoneNumber; //text field for the phone number that the user wishes to message or call
    String phonenumber; //phone number that should be contacted
    TextView tvFriendsTitle; //title to display name of current activity
    ImageButton btnCall; //button that allows user to call given phone number
    // declare current user
    User mCurrentUser;
    static final int MAX_SMS_MESSAGE_LENGTH = 160; //max length for a message before it is split

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    //initializing variables to populate the friend recycler view
    FriendsAdapter friendAdapter;
    ArrayList<Friend> friends;
    RecyclerView rvChatFriendList;

    //initializing variables to populate a chat button for each friend object
    ChatAdapter chatAdapter;
    ArrayList<Friend> chats;
    RecyclerView rvChatList;

    //variables for adding functionality where two recycler views scroll together
    NestedScrollView nvMine;

    private RecyclerView.OnScrollListener myScrollListener;
    private RecyclerView.OnItemTouchListener myTouchListener;

    int mTouchedRvTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //check for permissions to allow user to call or message
        checkPermissionsPlease();

        //initialize the send button and intent to send a message
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check to make sure the phone number is not invalid input (null or too long)
                if (etPhoneNumber.getText().toString() == null ||
                        etPhoneNumber.getText().toString().length() != 10) {
                    Toast.makeText(ContactActivity.this,
                            "You must enter a phone number first!", Toast.LENGTH_LONG).show();
                } else {
                    //send message to the given phone number with the pre-set text
                    sendSMS(etPhoneNumber.getText().toString(), etMessage.getText().toString());
                    Toast.makeText(ContactActivity.this, "Message sent!",
                            Toast.LENGTH_LONG).show();
                }
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

        nvMine = findViewById(R.id.nvMine);

        // Get the current user and cast appropriately.
        mCurrentUser = (User) ParseUser.getCurrentUser();
        phonenumber = mCurrentUser.getPhonNumber();
        etMessage = findViewById(R.id.etMessage);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        //initialize the bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_contact);
        bottomNavigationView.setSelectedItemId(R.id.action_message);
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

        rvChatFriendList = (RecyclerView) findViewById(R.id.rvChatFriendList);
        friends = new ArrayList<>();
        // construct the adapter from this data source
        friendAdapter = new FriendsAdapter(friends);
        // recycler view setup
        rvChatFriendList.setLayoutManager(new LinearLayoutManager(this));
        rvChatFriendList.setAdapter(friendAdapter);

        rvChatList = (RecyclerView) findViewById(R.id.rvChatList);
        chats = new ArrayList<>();
        chatAdapter = new ChatAdapter(friends);
        rvChatList.setLayoutManager(new LinearLayoutManager(this));
        rvChatList.setAdapter(chatAdapter);

        populateFriendList();

        rvChatFriendList.setTag(0);
        rvChatList.setTag(1);

        myScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ((int) recyclerView.getTag() == mTouchedRvTag) {
                    for (int noOfRecyclerView = 0; noOfRecyclerView < 2; noOfRecyclerView++) {
                        if (noOfRecyclerView != (int) recyclerView.getTag()) {
                            RecyclerView tempRecyclerView
                                    = (RecyclerView) nvMine.findViewWithTag(noOfRecyclerView);
                            tempRecyclerView.scrollBy(dx, dy);
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        };


        myTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                mTouchedRvTag = (int) rv.getTag();
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        };
        rvChatFriendList.addOnScrollListener(myScrollListener);
        rvChatList.addOnScrollListener(myScrollListener);

        rvChatFriendList.addOnItemTouchListener(myTouchListener);
        rvChatList.addOnItemTouchListener(myTouchListener);
    }

    /**
     * check to see if permissions are enabled, and if not, to prompt user to enable the permissions
     */
    private void checkPermissionsPlease() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
    }

    /**
     * @param phoneNumber phone number to send sms to
     * @param message     the message that should be sent through SMS
     */
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

    /**
     * function to allow user to dial a given phone number
     *
     * @param phoneNumber the phone number that should be called
     */
    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    /**
     * takes user to the settings page
     *
     * @param view button that takes user to settings
     */
    public void onSettings(View view) {
        Intent intent = new Intent(ContactActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * populate the friends adapter and chat adapter to display a list of friends that allow access to chat pages
     */
    public void populateFriendList() {
        if (mCurrentUser != null && mCurrentUser.getFriends() != null) {
            for (int i = 0; i < mCurrentUser.getFriendUsers().size(); i++) {
                Friend newFriend = mCurrentUser.getFriends().get(i);
                friends.add(newFriend);
                chats.add(newFriend);
                friendAdapter.notifyDataSetChanged();
            }
            chatAdapter.notifyDataSetChanged();
        }
    }
}
