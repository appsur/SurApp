package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.adapters.events.EventFriendsAdapter;
import com.pusheenicorn.safetyapp.adapters.events.EventUsersAdapter;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends BaseActivity {
    //declared variables
    ImageButton ibBanner;
    ImageButton ibAddMembers;
    Button btnSendAlert;
    ImageButton ibSearch;
    TextView tvEventTitle;
    EditText tvMessage;
    TextView tvEmergencyAlert;
    EditText etUsername;
    //declare bottom navigation view
    BottomNavigationView bottomNavigationView;
    Button btnSend;
    NotificationUtil notificationUtil;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();

    static final int REQUEST_CODE = 1;
    static final String TAG = "Success";
    private static int RESULT_LOAD_IMAGE = 1;
    Event currentEvent;
    Context context;

    EventUsersAdapter eventUsersAdapter;
    ArrayList<User> users;
    RecyclerView rvUsers;

    EventFriendsAdapter eventFriendsAdapter;
    ArrayList<Friend> friends;
    RecyclerView rvFriends;

    User currentUser;
    User user;
    List<Alert> alerts;

    private static String KEY_USERNAME = "username";
    private static String KEY_BANNER = "bannerimage";
    private static String KEY_EVENT = "event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        // Set the context
        context = this;
        // Get the current event and cast appropriately.
        currentEvent = (Event) getIntent().getParcelableExtra(KEY_EVENT);
        tvEventTitle = findViewById(R.id.tvEventTitle);
        etUsername = (EditText) findViewById(R.id.etUsername);
        ibAddMembers = (ImageButton) findViewById(R.id.ibAddMembers);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        btnSendAlert = (Button) findViewById(R.id.btnSendAlert);
        //added title to the event page
        tvEventTitle.setText(currentEvent.getName());
        tvMessage = (EditText) findViewById(R.id.tvMessage);
        //initialize bottom navigation bar
        bottomNavigationView = findViewById(R.id.bottom_navigation_events);
        setNavigationDestinations(EventsActivity.this, bottomNavigationView);
        btnSend = (Button) findViewById(R.id.btnSend);
        notificationUtil = new NotificationUtil(context, currentUser);

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
                        EventsActivity.this, mNavItems);
            }
        });

        //set banner to allow user to access gallery
        ibBanner = (ImageButton) findViewById(R.id.ibBanner);
        ParseFile bannerImage = currentEvent.getParseFile(KEY_BANNER);

        if (bannerImage != null)
        {
            //load image using glide
            Glide.with(EventsActivity.this).load(bannerImage.getUrl())
                    .into(ibBanner);
        }

        ibBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a picture chooser from gallery
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        // Get the current user
        currentUser = (User) ParseUser.getCurrentUser();

        // Find recycler views and hook up adapter
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        users = new ArrayList<User>();
        // construct the adapter from this data source
        eventUsersAdapter = new EventUsersAdapter(users);
        // recycler view setup
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(eventUsersAdapter);

        // Find recycler views and hook up adapter
        rvFriends = (RecyclerView) findViewById(R.id.rvFriends);
        friends = new ArrayList<Friend>();
        // construct the adapter from this data source
        eventFriendsAdapter = new EventFriendsAdapter(friends);
        // recycler view setup
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(eventFriendsAdapter);

        getEmergencyNotifications();
        // Populate the recycler views appropriate
        loadEventUsers();
    }

    public void getEmergencyNotifications() {
        alerts = currentEvent.getAlerts();
        if (alerts == null)
        {
            return;
        }
        else {
            for (int i = 0; i < alerts.size(); i++)
            {
                Alert curr = alerts.get(i);
                if (curr.getSeenBy() == null ||
                        !curr.getSeenBy().contains(currentUser.getObjectId()))
                {
                    // Schedule a notification for this alert
                    try {
                        String message = curr.fetchIfNeeded().getString(Alert.KEY_USERNAME) +
                                " says: " + curr.getMessage();
                        notificationUtil
                                .scheduleNotification(notificationUtil
                                        .getAlertNotification(message), 0);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Mark it as seen by this user
                    curr.addSeenBy(currentUser.getObjectId());
                    // Save the alert state to Parse
                    try {
                        curr.save();
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadEventUsers() {
        final User.Query userQuery = new User.Query();
        userQuery.getTop();

        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i > -1; i--) {
                        // If this user belongs to the event
                        if (currentEvent.getUsersIds().contains(objects.get(i).getObjectId())) {
                            // If this user is a friend, then...
                            if (currentUser.getFriendUsers().contains(objects.get(i).getObjectId()))
                            {
                                int index = currentUser.getFriendUsers()
                                        .indexOf(objects.get(i).getObjectId());
                                Friend newFriend = currentUser.getFriends().get(index);
                                friends.add(newFriend);
                                eventFriendsAdapter.notifyDataSetChanged();
                            }
                            else {
                                users.add(objects.get(i));
                                // notify the adapter
                                eventUsersAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if request code matches and the data is not null, set the image bitmap to be that of the picture
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            //get file path from the URI
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //set the banner to the image that is selected by the user
            ibBanner.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //convert bitmap to a parsefile
            final ParseFile parseFile = conversionBitmapParseFile(BitmapFactory.decodeFile(picturePath));
            //save in background so the image updates correctly
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    currentEvent.setBannerImage(parseFile);
                    currentEvent.put("bannerimage", parseFile);
                    currentEvent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                currentEvent.saveInBackground();
                                Log.d("EventsActivity", "Successfully updated!");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    //converts bitmap to parse file
    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png", imageByte);
        return parseFile;
    }

    public void onAddUser(View view) {
        etUsername.setVisibility(View.VISIBLE);
        ibAddMembers.setVisibility(View.INVISIBLE);
        ibSearch.setVisibility(View.VISIBLE);
    }


    public void onSearchUser(View view) {

        String username = etUsername.getText().toString();
        // Do not allow user to add themselves to an event that they are already part of!
        if (username == currentUser.getUsername()) {
            Toast.makeText(this, "Sorry, you are already part of this event!",
                    Toast.LENGTH_LONG).show();
            etUsername.setVisibility(View.INVISIBLE);
            ibAddMembers.setVisibility(View.VISIBLE);
            ibSearch.setVisibility(View.INVISIBLE);
            return;
        }

        // Allow them to enter other users as long as they can provide a valid username.
        final User.Query userQuery = new User.Query();
        userQuery.getTop().whereEqualTo(KEY_USERNAME, username);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    if (objects != null) {
                        currentEvent.addUser(objects.get(0));
                        currentEvent.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                eventFriendsAdapter.clear();
                                eventUsersAdapter.clear();
                                loadEventUsers();
                                etUsername.setVisibility(View.INVISIBLE);
                                ibAddMembers.setVisibility(View.VISIBLE);
                                ibSearch.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                    // If the username is invalid, toast a message to this effect.
                    else {
                        Toast.makeText(context, "Sorry, you entered an invalid username.",
                                Toast.LENGTH_LONG).show();
                        etUsername.setVisibility(View.INVISIBLE);
                        ibAddMembers.setVisibility(View.VISIBLE);
                        ibSearch.setVisibility(View.INVISIBLE);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onSendAlert(View view) {
        tvMessage.setVisibility(View.VISIBLE);
        btnSend.setVisibility(View.VISIBLE);
    }

    public void onSend(View view) {
        String message = tvMessage.getText().toString();
        final Alert alert = new Alert();
        alert.setMessage(message);
        alert.setName(currentUser.getName());
        alert.setUserame(currentUser.getUsername());
        alert.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                currentEvent.addAlert(alert);
                currentEvent.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        tvMessage.setVisibility(View.INVISIBLE);
                        btnSend.setVisibility(View.INVISIBLE);
                        getEmergencyNotifications();
                    }
                });
            }
        });
    }
}

