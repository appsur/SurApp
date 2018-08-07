package com.pusheenicorn.safetyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    public static final int MAX_LENGTH = 16;
//    public static String KEYWORD;
    // Declare views
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
//    String keyword;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();
    List<Friend> friendList;

    TextView tvBlocked;
    ImageView ivBlur;
    ImageButton ibPhone;
    ImageButton ibAlert;
    ImageButton ibUnfriend;
    Boolean isRinging;
    Context context;
    TextView alarm_text;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    static User friendUser;
    static Friend friend;
    User currentUser;
    User unFriendUser;
    boolean trackable;
    ParseGeoPoint loc;
    ParseGeoPoint lic;
    Location mCurrentLocation;
    private long Update_interval = 60000;
    private long Fastest_interval = 5000;
    private static final LatLng home = new LatLng(33.870851, -84.219635);
    private static final LatLng nick = new LatLng(33.870025, -84.219911);
    private static final LatLng wrc = new LatLng(29.716386, -95.398692);
    private final static String Key_location = "location";
    TextView tvPhone;
    TextView toolbar_title;
    AudioManager myAudioManager;
    ToggleButton tbGPS;
    ToggleButton tbMike;

    NotificationUtil notif;
    String userNum;
    boolean ringable;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

//    IntentFilter intentFilter;
//
//    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            keyword = intent.getExtras().getString("keyword");
////        }
//        }
//    };

//    String KEYWORD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

//        KEYWORD = random();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");

        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        currentUser = (User) ParseUser.getCurrentUser();
        context = getApplicationContext();

        boolean isNotif = getIntent().getBooleanExtra("notif", false);
        if (isNotif && (getIntent().getParcelableExtra("friend") != null)) {
            friend = getIntent().getParcelableExtra("friend");
        }
        else if (Parcels.unwrap(getIntent().getParcelableExtra(Friend.class.getSimpleName())) != null) {
            friend = (Friend) Parcels.unwrap(getIntent().getParcelableExtra(Friend.class.getSimpleName()));
        }

        friendUser = (User) friend.getUser();
        userNum = friendUser.getPhonNumber();
        trackable = friendUser.getCheckMe();
        ringable = friendUser.getRingable();

        if (trackable) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }
            ivBlur = (ImageView) findViewById(R.id.ivBlur);
            tvBlocked = (TextView) findViewById(R.id.tvBlocked);
            ivBlur.setVisibility(View.INVISIBLE);
            tvBlocked.setVisibility(View.INVISIBLE);

        }
        else{
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);
                    }
                });
            }
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
        // Logic for bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation_more);
        bottomNavigationView.setSelectedItemId(R.id.action_friends);
        setNavigationDestinations(MapActivity.this, bottomNavigationView);

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
                        MapActivity.this, mNavItems);
            }
        });

        ibUnfriend = (ImageButton) findViewById(R.id.ibUnfriend);
        ibUnfriend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                unfriend(currentUser);
            }
        });


        tvPhone = (TextView) findViewById(R.id.tvPhone);

        String number = "(" + friendUser.getPhonNumber().substring(0, 3) + ") " +
                friendUser.getPhonNumber().substring(3, 6) + "-" +
                friendUser.getPhonNumber().substring(6, 10);
        tvPhone.setText(number);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(friend.getName());

        ibPhone = findViewById(R.id.ibPhone);
        ibPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialFriendPhone(friendUser.getPhonNumber());
            }
        });

        ibAlert = findViewById(R.id.ibAlert);
        alarm_text = findViewById(R.id.alarm_text);
        ibAlert.setVisibility(View.INVISIBLE);
        alarm_text.setVisibility(View.INVISIBLE);
        
        if (ringable) {
            ibAlert.setVisibility(View.VISIBLE);
            alarm_text.setVisibility(View.VISIBLE);
        } else {
            ibAlert.setVisibility(View.INVISIBLE);
            alarm_text.setVisibility(View.INVISIBLE);
        }
        ibAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
//                // Check if the notification policy access has been granted for the app.
//                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
//                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//                    startActivity(intent);
//                }
                sendSMS();

            }
        });
        notif = new NotificationUtil(MapActivity.this, currentUser);
        notif.createNotificationChannel();
        //intent filter allows activity to know what the broadcast receiver can respond to
        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");
    }



    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


    }


    public void unfriend(User currentUser){
        List<Friend> friendList = currentUser.getFriends();
        List<Friend> toRemove = new ArrayList<Friend>();

        String id_B = friendUser.getObjectId();
        for (Friend frnd : friendList){
            unFriendUser = (User) frnd.getUser();
            String id_A = unFriendUser.getObjectId();
            if(id_A.equals(id_B)){
                toRemove.add(frnd);
            }
        }
        friendList.removeAll(toRemove);


        currentUser.setFriends(friendList);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent back = new Intent(MapActivity.this,
                        FriendsActivity.class);
                startActivity(back);
                finish();
            }
        });
    }

    public void onSettings(View view) {
        Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
        startActivity(intent);
    }



    protected void loadMap(GoogleMap googleMap) {
        loc = currentUser.getPlace();
        LatLng you = new LatLng(loc.getLatitude(), loc.getLongitude());
        lic = friendUser.getPlace();
        LatLng them = new LatLng(lic.getLatitude(), lic.getLongitude());
        map = googleMap;

        if (map != null) {
            // Map is ready
            googleMap.addMarker(new MarkerOptions().position(them)
                    .title(friendUser.getName()));
            googleMap.addMarker(new MarkerOptions().position(you)
                    .title(currentUser.getName()));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(you , 18));

            map.animateCamera(CameraUpdateFactory.zoomIn());

            map.animateCamera(CameraUpdateFactory.zoomTo(10) , 10000 , null);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(them)
                    .zoom(18)
                    .bearing(90)
                    .tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        } else {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ibSettings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //method for dialing the contact number provided
    private void dialFriendPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private void sendSMS() {
        String keyword = random();
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("friend", friend);
        friendUser.setKeyword(keyword);
        friendUser.saveInBackground();
//        Intent sendKeyword = new Intent("my.action.string");
//        sendKeyword.putExtra("keyword", KEYWORD);
//        sendBroadcast(sendKeyword);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(userNum, null, keyword, pi, null);
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


}
