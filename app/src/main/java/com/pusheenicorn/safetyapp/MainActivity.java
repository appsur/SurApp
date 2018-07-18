package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Declare views
    BottomNavigationView bottomNavigationView;
    ImageButton ibEvents;
    ImageButton ibProfileImage;
    TextView tvName;
    TextView tvUsername;
    TextView tvCheckinTime;
    TextView tvRelativeCheckinTime;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Logic for bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        ibEvents = (ImageButton) findViewById(R.id.ibEvents);
        ibEvents.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent goEvents = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(goEvents);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        // TODO -- link activities
                        return true;
                    case R.id.action_message:
                        // TODO -- link activities
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(MainActivity.this, FriendsActivity.class);
                        Toast.makeText(MainActivity.this, "Friends Page Accessed", Toast.LENGTH_LONG ).show();
                        startActivity(friendsAction);
                        return true;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_person);

        // Get current user.
        currentUser = (User) ParseUser.getCurrentUser();

        // Initialize views.
        tvCheckinTime = (TextView) findViewById(R.id.tvCheckinTime);
        tvRelativeCheckinTime = (TextView) findViewById(R.id.tvRelativeCheckinTime);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvName = (TextView) findViewById(R.id.tvName);
        ibProfileImage = (ImageButton) findViewById(R.id.ibProfileImage);

        tvUsername.setText(currentUser.getUserName());
        tvName.setText(currentUser.getName());

//        Date date = currentUser.getLastCheckin();
//        long dateint = date.getTime();
//        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
//        String formatedDate = dateFormat.format(date);
//        String newString = getRelativeTimeAgo(formatedDate);
//        tvRelativeCheckinTime.setText(newString);
    }

    public void onSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        Toast.makeText(MainActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG ).show();
        startActivity(intent);
    }

    public static String getRelativeTimeAgo(String rawDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
