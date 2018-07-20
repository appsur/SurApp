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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.pusheenicorn.safetyapp.models.User;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private static final String TAG = "MainActivity";

    // Define channel id for checkin notifications
    private static final String CHANNEL_ID = "checkin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissionsPlease();

        // Create notification channel for notifications.
        createNotificationChannel();
        // Set default values
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
                        Toast.makeText(MainActivity.this, "Message Page Accessed",
                                Toast.LENGTH_LONG).show();
                        startActivity(contactAction);
                        finish();
                        return true;
                    case R.id.action_emergency:
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                "6304862146", null)));
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(MainActivity.this,
                                FriendsActivity.class);
                        Toast.makeText(MainActivity.this, "Friends Page Accessed",
                                Toast.LENGTH_LONG).show();
                        startActivity(friendsAction);
                        finish();
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
        tvRelativeCheckinTime = (TextView) findViewById(R.id.tvRelativeCheckinTime);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvName = (TextView) findViewById(R.id.tvName);
        ibProfileImage = (ImageButton) findViewById(R.id.ibProfileImage);
        ibCheckin = (ImageButton) findViewById(R.id.ibCheckin);

        // Set values.
        tvUsername.setText(currentUser.getUserName());
        tvName.setText(currentUser.getName());
        Glide.with(context).load(currentUser.getProfileImage().getUrl()).into(ibProfileImage);

//
//        //check to see if the phone's gps is enabled
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        //retrieve user location
//        if (ActivityCompat.checkSelfPermission(this,
// android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
// && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
// != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            //return;
//
//
//        }
//
//        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
//
//        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
//
//        // Get Current Location
//        if (ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // No explanation needed, we can request the permission.
//
//            requestPermissions(
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
//
//            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//            // app-defined int constant. The callback method gets the
//            // result of the request.
//        } else {
//            //Permission is granted
//        }





        // Location myLocation = locationManager.getLastKnownLocation(provider);
//
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your devide",
                    Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
//
////


//        @Override
//        public void onRequestPermissionsResult(int requestCode, String[] permissions,
// int[] grantResults)
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
////
//       if (location != null) {
//            double longitude = location.getLongitude();
//            double latitude = location.getLatitude();
//            //store the user's location
//            final ParseGeoPoint point = new ParseGeoPoint(latitude , longitude);
//            currentUser.setPlace(point);
//
//            currentUser.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(com.parse.ParseException e) {
//
//                }
//            });
//        }





    }

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
                        // Immediately issue a notification to the user.
                        scheduleNotification(getNotification(), 0);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    //this will open a prompt to let the user know that gps is not enabled on their phone and will
    //allow the user to turn it on
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

    public void onSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        // Toast.makeText(MainActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    // Returns the relative time string.
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


    // Performs the checkin procedure.
    public void onCheckin(View view) {

        // Initialize some checkins for later use.
        final Checkin checkin;
        final Date newCheckinDate;

//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        //Location loc;
//        //double lat = loc.getLatitude()
//
//        LocationListener locationListener = new MyLocationListener();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
// != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        if (!isCheckedIn ){
            ibCheckin.setImageResource(R.drawable.check);
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
                                    final User user = (User) ParseUser.getCurrentUser();
                                    user.setLastCheckin(checkin);
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
        else {
            Toast.makeText(this, "You are already checked in!",
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

        if (trueCurr - truPrev > threshold) {
            return false;
        } else {
            return true;
        }
    }


    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //editLocation.setText("");
            //pb.setVisibility(View.INVISIBLE);
            Toast.makeText(
                    getBaseContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v(TAG, latitude);

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            //editLocation.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
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
        Toast.makeText(context, "Going to receiver??", Toast.LENGTH_LONG).show();
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

    public void onRefresh(View v)
    {
        onResume();
    }

}
