package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.User;

import java.io.Serializable;

public class SettingsActivity extends AppCompatActivity {

    // Define bottom navigation view.
    BottomNavigationView bottomNavigationView;

    // Define variables for image toggle.
    ImageButton ibClock;
    ImageButton ibLocator;
    ImageButton ibCompass;
    ImageButton ibAlert;
    boolean isClock = false;

    // Define variables for making frequency buttons appear.
    Button btnHourly;
    Button btnDaily;
    Button btnWeekly;

//    public File photoFile;
//    Uri photoURI;
//    static final int REQUEST_TAKE_PHOTO = 1;
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private static final String AUTHORITY = "com.pusheenicorn.sur-app";

    // Define global current user.
    User currentUser;

    // Define Text Views
    TextView tvNameValue;
    TextView tvPhoneValue;
    TextView tvUsernameValue;

    //button for logging out
    private ImageButton logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //log out button implementation
        logOutButton = findViewById(R.id.btnLogOut);
        logOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent logOut = new Intent(SettingsActivity.this, HomeActivity.class);
                ParseUser.logOut();
                startActivity(logOut);
            }
        });

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

        // Initialize buttons for frequency setup.
        btnHourly = (Button) findViewById(R.id.btnHourly);
        btnWeekly = (Button) findViewById(R.id.btnWeekly);
        btnDaily = (Button) findViewById(R.id.btnDaily);

        // Initialize buttons for text view setup.
        tvUsernameValue = (TextView) findViewById(R.id.tvUsernameValue);
        tvPhoneValue = (TextView) findViewById(R.id.tvPhoneValue);
        tvNameValue = (TextView) findViewById(R.id.tvNameValue);

        // Get the current user and cast appropriately.
        currentUser = (User) ParseUser.getCurrentUser();

        // Set initial values for text views.
        tvUsernameValue.setText(currentUser.getUserName());
        tvNameValue.setText(currentUser.getName());

        // Format the phone number and set the text view.
        String phoneNumber = currentUser.getPhonNumber();
        phoneNumber = "(" + phoneNumber.substring(0, 3) + ") "
                + phoneNumber.substring(3, 6) + " - "
                + phoneNumber.substring(6, 10);
        tvPhoneValue.setText(phoneNumber);

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
        if (!currentUser.getTrackable()) {
                ibLocator.setImageResource(R.drawable.ic_vector_location);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            final User user = (User) ParseUser.getCurrentUser();
                            user.setTrackable(true);
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
                        final User user = (User) ParseUser.getCurrentUser();
                        user.setTrackable(false);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onCompass(View view) {
        if (!currentUser.getRingable()) {
            ibCompass.setImageResource(R.drawable.compass);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final User user = (User) ParseUser.getCurrentUser();
                        user.setRingable(true);
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
                        final User user = (User) ParseUser.getCurrentUser();
                        user.setRingable(false);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onAlert(View view) {
        if (!currentUser.getSafe()) {
            ibAlert.setImageResource(R.drawable.bell);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        final User user = (User) ParseUser.getCurrentUser();
                        user.setSafe(true);
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
                        final User user = (User) ParseUser.getCurrentUser();
                        user.setSafe(false);
                        user.saveInBackground();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
