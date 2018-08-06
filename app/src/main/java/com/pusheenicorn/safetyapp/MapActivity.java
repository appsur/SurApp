package com.pusheenicorn.safetyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    // Declare views
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();
    List<Friend> friendList;

    TextView tvBlocked;
    ImageView ivBlur;
    ImageButton ibPhone;
    ImageButton ibAlert;
    ImageButton ibUnfriend;
    Boolean isRinging;
    Context context;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    User friendUser;
    Friend friend;
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


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        currentUser = (User) ParseUser.getCurrentUser();
        context = getApplicationContext();

        boolean isNotif = getIntent().getBooleanExtra("notif", false);
        if (isNotif) {
            friend = getIntent().getParcelableExtra("friend");
        }
        else {
            friend = (Friend) Parcels.unwrap(getIntent().getParcelableExtra(Friend.class.getSimpleName()));
        }

        friendUser = (User) friend.getUser();

        trackable = friendUser.getCheckMe();

        if (trackable) {

            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            //mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            if (mapFragment != null) {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        loadMap(map);


                    }
                });
            } else {
                Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
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

                //boolean f =friendList.remove(friend);
                //Toast.makeText(context ,f + " " , Toast.LENGTH_SHORT).show();
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
        ibAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
                // Check if the notification policy access has been granted for the app.
                if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
                sendSMS();

            }
        });
        notif = new NotificationUtil(MapActivity.this, currentUser);
        notif.createNotificationChannel();
    }



    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


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
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            //Move camera instantly to chosen location
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(home , 18));
            //have the camera zoom in
            //map.animateCamera(CameraUpdateFactory.zoomIn());


            //zoom out to zoom level 10, animating with a duration of 2 seconds
            //map.animateCamera(CameraUpdateFactory.zoomTo(10), 6000, null);





            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
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
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MapActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(friendUser.getPhonNumber(), null, "SUR", pi, null);
        //check to see if the length is above the maximum character length, and if so, to divide message
            sms.sendTextMessage(friendUser.getPhonNumber(), null, "SUR", pi, null);
    }


}
