package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

import static com.google.android.gms.maps.CameraUpdateFactory.zoomTo;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{


    BottomNavigationView bottomNavigationView;
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


    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

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
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent returnHome = new Intent(MapActivity.this, MainActivity.class);
                        startActivity(returnHome);
                        return true;

                    case R.id.action_message:
                        // TODO -- link activities
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(MapActivity.this, FriendsActivity.class);
                        Toast.makeText(MapActivity.this, "Friends Page Accessed", Toast.LENGTH_LONG ).show();
                        startActivity(friendsAction);
                        return true;
                }
                return true;
            }
        });
        friend = (Friend) Parcels.unwrap(getIntent().getParcelableExtra(Friend.class.getSimpleName()));
        currentUser = (User) ParseUser.getCurrentUser();
        friendUser = (User) friend.getUser();



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);

    }



    public void onSettings(View view) {
        Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
        Toast.makeText(MapActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG ).show();
        startActivity(intent);
    }

    protected void loadMap(GoogleMap googleMap) {
        loc = currentUser.getPlace();
        LatLng you = new LatLng(loc.getLatitude(), loc.getLongitude());
        lic = friendUser.getPlace();
        LatLng them = new LatLng(lic.getLatitude(), lic.getLongitude());
        map = googleMap;
        LatLngBounds two = new LatLngBounds( them,you);
        if (map != null) {
            // Map is ready
            googleMap.addMarker(new MarkerOptions().position(them)
                    .title("nick's house"));
            googleMap.addMarker(new MarkerOptions().position(you)
                    .title("Not Jared's House"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(you));
            //Move camera instantly to chosen location
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(home , 18));
            //have the camera zoom in
            //map.animateCamera(CameraUpdateFactory.zoomIn());
            map.setLatLngBoundsForCameraTarget(two);

            //zoom out to zoom level 10, animating with a duration of 2 seconds
            map.animateCamera(CameraUpdateFactory.zoomTo(10) , 2000 ,null);


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(nick)      // Sets the center of the map to Mountain View
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



}
