package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    // Define bottom navigation view.
    BottomNavigationView bottomNavigationView;

    // Define variables for image toggle.
    ImageButton ibClock;
    ImageButton ibLocator;
    ImageButton ibCompass;
    ImageButton ibAlert;
    boolean isClock = false;
    boolean isLocator = false;
    boolean isCompass = false;
    boolean isAlert = false;

    // Define variables for making frequency buttons appear.
    Button btnHourly;
    Button btnDaily;
    Button btnWeekly;
    public File photoFile;
    Uri photoURI;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String AUTHORITY = "com.pusheenicorn.sur-app";

    // Define global current user.
    ParseUser currentUser;

    // Define Text Views
    TextView tvNameValue;
    TextView tvPhoneValue;
    TextView tvUsernameValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Implementation of bottom navigation view.
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent personAction = new Intent(SettingsActivity.this,
                                MainActivity.class);
                        Toast.makeText(SettingsActivity.this, "Main Page Accessed",
                                Toast.LENGTH_LONG ).show();
                        startActivity(personAction);
                        return true;
                    case R.id.action_message:
                        // TODO -- link activities
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(SettingsActivity.this,
                                FriendsActivity.class);
                        Toast.makeText(SettingsActivity.this, "Friends Page Accessed",
                                Toast.LENGTH_LONG ).show();
                        startActivity(friendsAction);
                        return true;
                }
                return true;
            }
        });

        // Initialize buttons for image toggle.
        ibClock = (ImageButton) findViewById(R.id.ibClock);
        ibLocator = (ImageButton) findViewById(R.id.ibLocator);
        ibCompass = (ImageButton) findViewById(R.id.ibCompass);
        ibAlert = (ImageButton) findViewById(R.id.ibAlert);

        // Initialize buttons for frequency setup
        btnHourly = (Button) findViewById(R.id.btnHourly);
        btnWeekly = (Button) findViewById(R.id.btnWeekly);
        btnDaily = (Button) findViewById(R.id.btnDaily);

        // Initialize buttons for textview setup
        tvUsernameValue = (TextView) findViewById(R.id.tvUsernameValue);
        tvPhoneValue = (TextView) findViewById(R.id.tvPhoneValue);
        tvNameValue = (TextView) findViewById(R.id.tvNameValue);

        // Set Text Views
        tvNameValue.setText(currentUser.getString("name"));
        tvUsernameValue.setText(currentUser.getUsername());
        tvNameValue.setText(currentUser.getString("phonenumber"));


        ParseUser.logInInBackground("grace", "password", new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("Login Activity", "Login successful");
                }
                else {
                    Log.d("Login Activity", "Login failure");
                    e.printStackTrace();
                }
            }
        });

        //////////////
        currentUser = ParseUser.getCurrentUser();
    }

    public void onClock(View view) {

        // UI response
        isClock = !isClock;
        if (isClock)
        {
            ibClock.setImageResource(R.drawable.clock);
            // Functionality
            btnHourly.setVisibility(View.VISIBLE);
            btnDaily.setVisibility(View.VISIBLE);
            btnWeekly.setVisibility(View.VISIBLE);
        }
        else {
            ibClock.setImageResource(R.drawable.clock_outline);
            // Functionality
            btnHourly.setVisibility(View.INVISIBLE);
            btnDaily.setVisibility(View.INVISIBLE);
            btnWeekly.setVisibility(View.INVISIBLE);
        }
    }

    public void onLocator(View view) {

        // UI response
        isLocator = !isLocator;
        if (isLocator) {
                ibLocator.setImageResource(R.drawable.ic_vector_location);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            final ParseUser user = ParseUser.getCurrentUser();
                            user.put("trackable", true);
                            user.saveInBackground();
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
        }
        else
        {
            ibLocator.setImageResource(R.drawable.ic_vector_location_stroke);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final ParseUser user = ParseUser.getCurrentUser();
                        user.put("trackable", false);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onCompass(View view) {

        // UI response
        isCompass = !isCompass;
        if (isCompass) {
            ibCompass.setImageResource(R.drawable.compass);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final ParseUser user = ParseUser.getCurrentUser();
                        user.put("ringable", true);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            ibCompass.setImageResource(R.drawable.compass_outline);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final ParseUser user = ParseUser.getCurrentUser();
                        user.put("ringable", false);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onAlert(View view) {

        //UI response
        isAlert = !isAlert;
        if (isAlert) {
            ibAlert.setImageResource(R.drawable.bell);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final ParseUser user = ParseUser.getCurrentUser();
                        user.put("safe", true);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            ibAlert.setImageResource(R.drawable.bell_outline);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final ParseUser user = ParseUser.getCurrentUser();
                        user.put("safe", false);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
