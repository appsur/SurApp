package com.pusheenicorn.safetyapp;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Declare views
    BottomNavigationView bottomNavigationView;
    ImageButton ibEvents;
    ImageButton ibProfileImage;
    ImageButton ibCheckin;
    TextView tvName;
    TextView tvUsername;
    TextView tvCheckinTime;
    TextView tvRelativeCheckinTime;
    User currentUser;
    Checkin checkin;
    Context context;
    boolean isCheckedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isCheckedIn = false;
        context = this;
        // Logic for bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        ibEvents = (ImageButton) findViewById(R.id.ibEvents);
        ibEvents.setOnClickListener(new View.OnClickListener() {

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
                        Intent messageAction = new Intent(MainActivity.this, ContactActivity.class);
                        Toast.makeText(MainActivity.this, "Message Page Accessed", Toast.LENGTH_LONG).show();
                        startActivity(messageAction);
                        finish();
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(MainActivity.this, FriendsActivity.class);
                        Toast.makeText(MainActivity.this, "Friends Page Accessed", Toast.LENGTH_LONG).show();
                        startActivity(friendsAction);
                        finish();
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
        ibCheckin = (ImageButton) findViewById(R.id.ibCheckin);

        // Set values.
        tvUsername.setText(currentUser.getUserName());
        tvName.setText(currentUser.getName());

        // Find time of last checkin by geting checkin id and making query.
        final String checkinId = currentUser.getLastCheckin().getObjectId();
        final Checkin.Query checkinQuery = new Checkin.Query();
        checkinQuery.getTop().whereEqualTo("objectId", checkinId);
        checkinQuery.findInBackground(new FindCallback<Checkin>() {
            @Override
            public void done(List<Checkin> objects, com.parse.ParseException e) {
                if (e == null) {
                    checkin = objects.get(0);
                    Date date = checkin.getCreatedAt();
                    DateFormat dateFormat =
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
                    String formatedDate = dateFormat.format(date);
                    String newString = getRelativeTimeAgo(formatedDate);
                    String[] formatedDateArr = formatedDate.split(" ");
                    formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] + " " +
                            formatedDateArr[2] +
                            " " + formatedDateArr[3];
                    tvRelativeCheckinTime.setText(newString);
                    tvCheckinTime.setText(formatedDate);
                    isCheckedIn = isCheckedIn(date);
                    if (isCheckedIn)
                    {
                        ibCheckin.setImageResource(R.drawable.check);
                    }
                    else
                    {
                        ibCheckin.setImageResource(R.drawable.check_outline);
                        Toast.makeText(context, "Click the check button to checkin!", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    e.printStackTrace();
                }
            }
        });

        Glide.with(context).load(currentUser.getProfileImage().getUrl()).into(ibProfileImage);
    }

    public void onSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        Toast.makeText(MainActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG ).show();
        startActivity(intent);
    }

    public String getRelativeTimeAgo(String rawDate) {
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

    public void onCheckin(View view) {
        final Checkin checkin;
        final Date newCheckinDate;
        if (!isCheckedIn)
        {
            ibCheckin.setImageResource(R.drawable.check);
            checkin = new Checkin();
            newCheckinDate = new Date();
            checkin.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null)
                    {
                        checkin.saveInBackground();
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null)
                    {
                        final User user = (User) ParseUser.getCurrentUser();
                        user.setLastCheckin(checkin);
                        user.saveInBackground();
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });

            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
            String formatedDate = dateFormat.format(newCheckinDate);
            String newString = getRelativeTimeAgo(formatedDate);
            String[] formatedDateArr = formatedDate.split(" ");
            formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] + " " +
                    formatedDateArr[2] + " " + formatedDateArr[3];
            tvRelativeCheckinTime.setText(newString);
            tvCheckinTime.setText(formatedDate);
            isCheckedIn = isCheckedIn(newCheckinDate);
            if (isCheckedIn)
            {
                ibCheckin.setImageResource(R.drawable.check);
            }
            else {
                ibCheckin.setImageResource(R.drawable.check_outline);
            }
        }

        else {
            Toast.makeText(context, "Thanks, but you've already checked in!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isCheckedIn(Date prevDate) {
        // Define format type.
        DateFormat df = new SimpleDateFormat("MM/dd/yy/HH/mm");

        // Get current Date.
        Date currDate = new Date();

        // Split by regex "/" convert to int array and find time difference.
        String[] currDateArr = df.format(currDate).split("/");
        String[] prevDateArr = df.format(prevDate).split("/");
        int[] currDateInts = new int[5];
        int[] prevDateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            currDateInts[i] = Integer.parseInt(currDateArr[i]);
            prevDateInts[i] = Integer.parseInt(prevDateArr[i]);
        }
        int trueCurr = (currDateInts[0] * 43800) + (currDateInts[1] * 1440)
                + (currDateInts[2] * 525600) + (currDateInts[3] * 60) + currDateInts[4];
        int truPrev = (prevDateInts[0] * 43800) + (prevDateInts[1] * 1440)
                + (prevDateInts[2] * 525600) + (prevDateInts[3] * 60) + prevDateInts[4];
        int threshold = (int) currentUser.getNumber("checkin");

        if (trueCurr - truPrev > threshold)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
