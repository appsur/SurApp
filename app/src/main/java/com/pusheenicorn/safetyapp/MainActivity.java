package com.pusheenicorn.safetyapp;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {
    // Declare views
    BottomNavigationView bottomNavigationView;
    ImageButton ibEvents;
    ImageButton ibProfileImage;
    ImageButton ibCheckin;
    ImageButton ibAddEvent;
    ImageButton ibConfirmEvent;
    TextView tvName;
    TextView tvUsername;
    TextView tvCheckinTime;
    TextView tvRelativeCheckinTime;
    TextView tvUpcomingActivities;
    EditText etStartTime;
    EditText etEndTime;
    EditText etEventName;
    EditText etEventLocation;

    // Declare fields
    User currentUser;
    Checkin checkin;
    Context context;
    boolean isCheckedIn;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private static final String TAG = "MainActivity";
    private Location mLocation = null;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;

    // Declare adapter, events list, and events adapter
    EventAdapter eventAdapter;
    ArrayList<Event> events;
    RecyclerView rvEvents;

    // Define channel id for checkin notifications
    private static final String CHANNEL_ID = "checkin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for geotracking permissions.
        checkPermissionsPlease();

        // Create notification channel for notifications.
        createNotificationChannel();

        // Set default values
        isCheckedIn = false;
        context = this;

        // Setup adapter logic
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
        events = new ArrayList<Event>();
        // construct the adapter from this data source
        eventAdapter = new EventAdapter(events);
        // recycler view setup
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);

        // Logic for bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

     //   getLocation();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        // TODO -- link activities
                        return true;
                    case R.id.action_message:
                        Intent contactAction = new Intent(MainActivity.this,
                                ContactActivity.class);
//                        Toast.makeText(MainActivity.this, "Message Page Accessed",
//                                Toast.LENGTH_LONG).show();
                        startActivity(contactAction);
                        // finish();
                        return true;
                    case R.id.action_emergency:
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                "6304862146", null)));
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(MainActivity.this,
                                FriendsActivity.class);
//                        Toast.makeText(MainActivity.this, "Friends Page Accessed",
//                                Toast.LENGTH_LONG).show();
                        startActivity(friendsAction);
                        // finish();
                        return true;
                }
                return true;
            }
        });

        // Indicate the current activity
        bottomNavigationView.setSelectedItemId(R.id.action_person);

        // Get the current user.
        currentUser = (User) ParseUser.getCurrentUser();

        // Initialize views.
        tvCheckinTime = (TextView) findViewById(R.id.tvCheckinTime);
        tvUpcomingActivities = (TextView) findViewById(R.id.tvUpcomingActivities);
        tvRelativeCheckinTime = (TextView) findViewById(R.id.tvRelativeCheckinTime);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvName = (TextView) findViewById(R.id.tvName);
        ibProfileImage = (ImageButton) findViewById(R.id.ibProfileImage);
        ibCheckin = (ImageButton) findViewById(R.id.ibCheckin);
        etEndTime = (EditText) findViewById(R.id.etEndTime);
        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etEventLocation = (EditText) findViewById(R.id.etEventLocation);
        etEventName = (EditText) findViewById(R.id.etEventName);
        ibAddEvent = (ImageButton) findViewById(R.id.ibAddEvent);
        ibConfirmEvent = (ImageButton) findViewById(R.id.ibConfirmEvent);

        // Set values.
        tvUsername.setText(currentUser.getUserName());
        tvName.setText(currentUser.getName());

        // Find time of last checkin by geting checkin id and making query.
        final String checkinId;
        checkinId = currentUser.getLastCheckin().getObjectId();
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
                    if (isCheckedIn) {
                        ibCheckin.setImageResource(R.drawable.check);
                    } else {
                        ibCheckin.setImageResource(R.drawable.check_outline);
                        //Toast.makeText(context, "Click the check button to checkin!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        Glide.with(context).load(currentUser.getProfileImage().getUrl()).into(ibProfileImage);
        populateEvents();

// JARED-------------------------------------------------------------------------------------------
//        //check to see if the phone's gps is enabled
        LocationManager locationManager = (LocationManager) getSystemService(context.LOCATION_SERVICE);

//
//        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
//
//        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        Location myLocation = locationManager.getLastKnownLocation(provider);
        //test
        //Toast.makeText(this, myLocation.getLatitude() + "hhh", Toast.LENGTH_SHORT).show();

//
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//           Toast.makeText(this, "GPS is Enabled in your device",
//                   Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
////        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);



//        @Override
//        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Toast.makeText(this, location.getLatitude() + "hhh", Toast.LENGTH_LONG).show();
//
       if (location != null) {
          double longitude = location.getLongitude();
           double latitude = location.getLatitude();
            //store the user's location
            final ParseGeoPoint point = new ParseGeoPoint(latitude , longitude);
            Toast.makeText(MainActivity.this, latitude + ":" + longitude, Toast.LENGTH_LONG).show();
            currentUser.setPlace(point);

            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {

                }
            });
           locationManager.requestLocationUpdates(bestProvider, 1000, 0, MainActivity.this);
        }else{
           //retrieve location from best existing source
           double longitude = myLocation.getLongitude();
           double latitude = myLocation.getLatitude();
           //store the user's location
           final ParseGeoPoint point = new ParseGeoPoint(latitude , longitude);
           Toast.makeText(MainActivity.this, latitude + ":" + longitude, Toast.LENGTH_LONG).show();
           currentUser.setPlace(point);

           currentUser.saveInBackground(new SaveCallback() {
               @Override
               public void done(com.parse.ParseException e) {

               }
           });
       }
}
// JARED -----------------------------------------------------------------------------------------



    // Update the checkin button each time the app is restarted/reopened in case the user made a
    // checkin through a push notification while the app was closed/in-background.
    @Override
    public void onResume(){
        // Do this after onCreate is executed
        super.onResume();

        // Find id of last checkin.
        final String checkinId;
        currentUser = (User) ParseUser.getCurrentUser();
        checkinId = currentUser.getLastCheckin().getObjectId();

        // Get the actual checkin object by making a query.
        final Checkin.Query checkinQuery = new Checkin.Query();
        checkinQuery.getTop().whereEqualTo("objectId", checkinId);
        checkinQuery.findInBackground(new FindCallback<Checkin>() {
            @Override
            public void done(List<Checkin> objects, com.parse.ParseException e) {
                if (e == null) {
                    // Get the checkin object and format its date
                    checkin = objects.get(0);
                    Date date = checkin.getCreatedAt();
                    DateFormat dateFormat =
                            new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
                    String formatedDate = dateFormat.format(date);
                    // Get the relative time difference
                    String newString = getRelativeTimeAgo(formatedDate);
                    String[] formatedDateArr = formatedDate.split(" ");
                    formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] + " " +
                            formatedDateArr[2] +
                            " " + formatedDateArr[3];
                    tvRelativeCheckinTime.setText(newString);
                    tvCheckinTime.setText(formatedDate);
                    // If the last checkin is not expired, set the green check. Otherwise, prompt
                    // the user to check in.
                    isCheckedIn = isCheckedIn(date);
                    if (isCheckedIn) {
                        ibCheckin.setImageResource(R.drawable.check);
                    } else {
                        ibCheckin.setImageResource(R.drawable.check_outline);
                        // Provide an in-app reminder.
                        Toast.makeText(context, "Click the check button to checkin!",
                                Toast.LENGTH_LONG).show();
                        scheduleNotification(getNotification(), 0);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // JARED --------------------------------------------------------------------------------------
    // this will open a prompt to let the user know that gps is not enabled on their phone and will
    // allow the user to turn it on
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. " +
                "Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    // JARED ---------------------------------------------------------------------------------------


    /**
     * This function populates the events adapter.
     */
    public void populateEvents() {
        // Make a new query.
        final Event.Query eventQuery = new Event.Query();
        // Get only the top 20 events in the database.
        eventQuery.getTop();
                //.whereEqualTo("usersAttending", currentUser);
        eventQuery.findInBackground(new FindCallback<Event>() {
            @Override
            public void done(List<Event> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i > -1; i--)
                    {
                        if (currentUser.getEvents() != null &&
                                currentUser.getEventIds().contains(objects.get(i).getObjectId()))
                        {
                            events.add(objects.get(i));
                            // notify the adapter
                            eventAdapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * When the user clicks the settings button, this function is called so that the settings
     * activity will be launched.
     *
     * @param view: the settings button
     */
    public void onSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        // Toast.makeText(MainActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    /**
     * This function returns the relative time between the current time and a passed in date
     *
     * @param rawDate: a String date formatted as "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
     * @return relativeDate: the relative time between rawDate and present
     */
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


    /**
     * When the user clicks the checkin button, this function is called so that the user's
     * last checkin information can be updated.
     *
     * @param view: the checkin button
     */
    public void onCheckin(View view) {

        // Initialize some checkins for later use.
        final Checkin checkin;
        final Date newCheckinDate;

        // JARED------------------------------------------------------------------------------------



        // JARED------------------------------------------------------------------------------------

        // If the user is elligible to checkin at this time...
        if (!isCheckedIn ){
            // Update the view to reflect a checked circle
            ibCheckin.setImageResource(R.drawable.check);
            // Create a new checkin object and save it in background
            checkin = new Checkin();
            newCheckinDate = new Date();
            checkin.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        checkin.saveInBackground();
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(com.parse.ParseException e) {
                                if (e == null) {
                                    // Set the current user to point to the newly created checkin
                                    // object as its last checkin.
                                    final User user = (User) ParseUser.getCurrentUser();
                                    user.setLastCheckin(checkin);
                                    // Save the user and update the textviews in the activity.
                                    user.saveInBackground();
                                    DateFormat dateFormat =
                                            new SimpleDateFormat("EEE MMM dd " +
                                                    "HH:mm:ss ZZZZZ yyyy");
                                    String formatedDate = dateFormat.format(newCheckinDate);
                                    String newString = getRelativeTimeAgo(formatedDate);
                                    String[] formatedDateArr = formatedDate.split(" ");
                                    formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] +
                                            " " + formatedDateArr[2] + " " + formatedDateArr[3];
                                    tvRelativeCheckinTime.setText(newString);
                                    tvCheckinTime.setText(formatedDate);
                                    isCheckedIn = isCheckedIn(newCheckinDate);
                                    if (isCheckedIn) {
                                        ibCheckin.setImageResource(R.drawable.check);
                                    }
                                    else {
                                        ibCheckin.setImageResource(R.drawable.check_outline);
                                    }
                                    // Schedule the next notification based on the user's
                                    // checkin frequency.
                                    int mins = (int) currentUser.getNumber("checkin");
                                    scheduleNotification(getNotification(), mins * 60000);
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });
        }
        // Otherwise, the user is not eligible to checkin, so toast a message to this effect.
        else {
            Toast.makeText(this, "You are already checked in!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This function takes in a Date and determines whether a checkin made at this dat has
     * expired or not.
     *
     * @param prevDate: the date at which the last checkin was made
     * @return true if the checkin is not expired, otherwise false
     */
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

        if (trueCurr - truPrev > threshold) {
            return false;
        } else {
            return true;
        }
    }


    // JARED---------------------------------------------------------------------------------------


    /*---------- Listener class to get coordinates ------------- */
        protected void getLocation(){
            if (isLocationEnabled(MainActivity.this)) {
                locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
                criteria = new Criteria();
                //bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
                bestProvider = locationManager.getBestProvider(criteria, true);
                //You can still do this if you like, you might get lucky:
                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e("TAG", "GPS is on");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();

                }
                else{
                    //This is what you need:
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
                }
            }
            }


        @Override
        public void onLocationChanged(Location loc) {
//            //editLocation.setText("");
//            //pb.setVisibility(View.INVISIBLE);
//            //remove location callback:
//            locationManager.removeUpdates(this);
//
//            //open the map:
//            latitude = loc.getLatitude();
//            longitude = loc.getLongitude();
//            //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
//
//        /*------- To get city name from coordinates -------- */
//            String cityName = null;
//            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
//            List<Address> addresses;
//            try {
//                addresses = gcd.getFromLocation(loc.getLatitude(),
//                        loc.getLongitude(), 1);
//                if (addresses.size() > 0) {
//                    System.out.println(addresses.get(0).getLocality());
//                    cityName = addresses.get(0).getLocality();
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
//                    + cityName;
//            Toast.makeText(MainActivity.this, s , Toast.LENGTH_SHORT).show();
//
//            //editLocation.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        // JARED-----------------------------------------------------------------------------------



    public static boolean isLocationEnabled(Context context)
    {
        //...............
        return true;
    }









//    public void makeNotification() {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.check)
//                .setContentTitle("My notification")
//                .setContentText("Hello World!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        notificationManager.notify(1234, mBuilder.build());
//    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification getNotification() {
        //Toast.makeText(context, "Going to receiver??", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra("actionName", "checkIn");
        intent.putExtra("user", currentUser);

        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 10,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Sûr")
                        .setSmallIcon(R.drawable.check)
                        .setContentText("Please remember to check in!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.check_outline, "Check in now", pendingIntent)
                        .setOngoing(true);
                        // builder.setContentIntent(pendingIntent);
                        // builder.setAutoCancel(true);
        return builder.build();
    }

    /**
     * This function takes a notification and delay and schedules the notification at current
     * time + delay.
     *
     * @param notification: the notification to be sent
     * @param delay: the delay at which to send the notification
     */
    public void scheduleNotification(Notification notification, int delay) {
        //Toast.makeText(this, "Scheduled notification in " + (delay / 60000)
                //+ " minutes", Toast.LENGTH_LONG).show();
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private void checkPermissionsPlease() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    /**
     * If the user tries to manually refresh the screen (this should really never be necessary)
     * then the onResume called is called.
     *
     * @param v: the parent view
     */
    public void onRefresh(View v)
    {
        onResume();
    }

    /**
     * Allow the user to make and add a new event.
     *
     * @param view: the add event button
     */
    public void onAddEvent(View view) {
        tvUpcomingActivities.setText("NEW EVENT");
        etEndTime.setVisibility(View.VISIBLE);
        etStartTime.setVisibility(View.VISIBLE);
        etEventLocation.setVisibility(View.VISIBLE);
        etEventName.setVisibility(View.VISIBLE);
        ibAddEvent.setVisibility(View.INVISIBLE);
        ibConfirmEvent.setVisibility(View.VISIBLE);
        rvEvents.setVisibility(View.INVISIBLE);
    }

    public void onConfirmEvent(View view) {
        final Event event = new Event();
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // Set the event attributes and save in background
                    event.setName(etEventName.getText().toString());
                    event.setStart(etStartTime.getText().toString());
                    event.setEnd(etEndTime.getText().toString());
                    event.setLocation(etEventLocation.getText().toString());
                    event.addUser(currentUser);
                    event.saveInBackground();
                    // Add the event to the events list, notify the adapter, and update views
                    events.add(event);
                    eventAdapter.notifyDataSetChanged();
                    tvUpcomingActivities.setText("UPCOMING EVENTS");
                    etEndTime.setVisibility(View.INVISIBLE);
                    etStartTime.setVisibility(View.INVISIBLE);
                    etEventLocation.setVisibility(View.INVISIBLE);
                    etEventName.setVisibility(View.INVISIBLE);
                    ibAddEvent.setVisibility(View.VISIBLE);
                    ibConfirmEvent.setVisibility(View.INVISIBLE);
                    rvEvents.setVisibility(View.VISIBLE);
                    // Add to the user's events
                    final User thisUser = (User) ParseUser.getCurrentUser();
                    thisUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            thisUser.addEvent(event);
                            thisUser.saveInBackground();
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
