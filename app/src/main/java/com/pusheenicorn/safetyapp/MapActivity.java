package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;

import static com.google.android.gms.maps.CameraUpdateFactory.zoomTo;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {
    // Declare views
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();

    ImageButton ibPhone;
    ImageButton ibAlert;
    Boolean isRinging;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    User friendUser;
    Friend friend;
    User currentUser;
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


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        ibAlert = findViewById(R.id.ibAlert);
        ibAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AudioManager audio = (AudioManager) MapActivity.this.getSystemService(Context.AUDIO_SERVICE);
//                int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
//                int max = audio.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
//                audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
//                audio.setStreamVolume(AudioManager.STREAM_RING, max, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                int volume = myAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
//                if(volume==0)
//                    volume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
//                myAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
//                ringtone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(ringTonePath));
//                if(ringtone!=null){
//                    ringtone.setStreamType(AudioManager.STREAM_ALARM);
//                    ringtone.play();
//                    isRinging = true;
//                }
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(MapActivity.this);
//                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                r.play();
            }
        });

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

        // Logic for bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation_more);
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


        currentUser = (User) ParseUser.getCurrentUser();
        if (currentUser == null){
            //currentUser = (User) getIntent().getSerializableExtra("user");
        }

        boolean isNotif = getIntent().getBooleanExtra("notif", false);
        if (isNotif) {
            friend = getIntent().getParcelableExtra("friend");
        }
        else {
            friend = (Friend) Parcels.unwrap(getIntent().getParcelableExtra(Friend.class.getSimpleName()));
        }

        friendUser = (User) friend.getUser();

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
        Toast.makeText(MapActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    protected void loadMap(GoogleMap googleMap) {
        loc = currentUser.getPlace();
        LatLng you = new LatLng(loc.getLatitude(), loc.getLongitude());
        lic = friendUser.getPlace();
        LatLng them = new LatLng(lic.getLatitude(), lic.getLongitude());
        map = googleMap;
        //for now this probably isn't necessary
        /*
        if (you.latitude > them.latitude){
            LatLngBounds two = new LatLngBounds( them,you);
            map.setLatLngBoundsForCameraTarget(two);
        }else{
            LatLngBounds zwei = new LatLngBounds( you , them);
            map.setLatLngBoundsForCameraTarget(zwei);
        }
        */
        if (map != null) {


            // Map is ready
            googleMap.addMarker(new MarkerOptions().position(them)
                    .title(friendUser.getName()));
            googleMap.addMarker(new MarkerOptions().position(you)
                    .title(currentUser.getName()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            //Move camera instantly to chosen location
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(home , 18));
            //have the camera zoom in
            //map.animateCamera(CameraUpdateFactory.zoomIn());


            //zoom out to zoom level 10, animating with a duration of 2 seconds
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(them)      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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

}
