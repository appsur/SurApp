package com.pusheenicorn.safetyapp;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.PlaceHolderView;
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

public class MainActivity extends BaseActivity implements LocationListener {

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

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

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
    private EventAdapter eventAdapter;
    private ArrayList<Event> events;
    private RecyclerView rvEvents;

    // Define channel id for checkin notifications
    private static final String CHANNEL_ID = "checkin";

    public static final String TWITTER_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionsPlease();                           // Check for geotracking permissions.
        createNotificationChannel();                        // Create notification channel.
        isCheckedIn = false;                                // Set default values
        context = this;                                     // Set default values
        setEventsAdapter();                                 // Set up the events adapter.
        // Set up the BNV.
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setNavigationDestinations(MainActivity.this, bottomNavigationView);
        currentUser = (User) ParseUser.getCurrentUser();    // Get the current user.
        initializeViews();                                  // Initialize the views.
        populateEvents();                                   // Populate the events recycler view.
        getLocation();                                      // Get the user's location.
        initializeNavItems(mNavItems);                      // Initialize navigation items
        setUpDrawerLayout();                                // Set up the pull-out menu.
    }

    /**
     * This function is called whenever the user restarts or reopens the main activity (e.g.
     * after a push notification interacted with the app) so we need to refresh some views.
     */
    @Override
    public void onResume() {
        // Do this after onCreate is executed
        super.onResume();
        findLastCheckin();
    }

    /**
     * This function sets up the drawer layout for this activity.
     */
    public void setUpDrawerLayout() {
        // Set up the drawerLayout
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
                        MainActivity.this, mNavItems);
            }
        });
    }

    /**
     * TODO -- ADD JAVADOC
     *
     * @param loc
     */
    public void saveLocation(Location loc) {
        //retrieve location from best existing source
        double longitude = loc.getLongitude();
        double latitude = loc.getLatitude();
        //store the user's location
        final ParseGeoPoint point = new ParseGeoPoint(latitude, longitude);
        Toast.makeText(MainActivity.this, latitude + ":" + longitude, Toast.LENGTH_LONG).show();
        currentUser.setPlace(point);

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {

            }
        });
    }

    /**
     * This function finds the user's last checkin and determines whether or not
     * to send a checkin reminder. It then sends the reminder if needed. It takes
     * no arguments.
     */
    public void findLastCheckin() {
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
                    checkin = objects.get(0);
                    Date date = checkin.getCreatedAt();                     // Get date of creation.
                    String formatedDate = DATE_FORMAT.format(date);
                    String newString = getRelativeTimeAgo(formatedDate);    // Get relative time.
                    formatedDate = getFormattedStringDate(formatedDate);    // Format nicely.
                    tvRelativeCheckinTime.setText(newString);               // Update TV's.
                    tvCheckinTime.setText(formatedDate);
                    // If the last checkin is not expired, set the green check. Otherwise, prompt
                    // the user to check in.
                    isCheckedIn = isCheckedIn(date);
                    if (isCheckedIn) {
                        ibCheckin.setImageResource(R.drawable.check);
                    } else {
                        ibCheckin.setImageResource(R.drawable.check_outline);
                        // Send an immediate notification.
                        scheduleNotification(getNotification(), 0);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This function takes a String that represents a date and formats it for display.
     *
     * @param formatedDate: the String to be formatted.
     * @return the formatted String.
     */
    public String getFormattedStringDate(String formatedDate) {
        String[] formatedDateArr = formatedDate.split(" ");
        formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] + " " +
                formatedDateArr[2] +
                " " + formatedDateArr[3];
        return formatedDate;
    }

    /**
     * This function initializes the views and some text fields
     * in the layout. It takes no arguments.
     */
    public void initializeViews() {
        // Find the views.
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
        // Set username + name.
        tvUsername.setText(currentUser.getUserName());
        tvName.setText(currentUser.getName());
        // Load the profile image.
        Glide.with(context).load(currentUser.getProfileImage().getUrl()).into(ibProfileImage);
    }

    /**
     * This function takes no arguments and sets up the events adapter.
     */
    public void setEventsAdapter() {
        // Setup adapter logic
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
        events = new ArrayList<Event>();
        // construct the adapter from this data source
        eventAdapter = new EventAdapter(events);
        // recycler view setup
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setAdapter(eventAdapter);
    }

    /**
     * TODO -- ADD JAVADOC
     */
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
                    for (int i = objects.size() - 1; i > -1; i--) {
                        if (currentUser.getEvents() != null &&
                                currentUser.getEventIds().contains(objects.get(i).getObjectId())) {
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
        SimpleDateFormat sf = new SimpleDateFormat(TWITTER_FORMAT, Locale.ENGLISH);
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

        if (isCheckedIn)
        {
            // If the user is checking in again when they are already checked in, we need to
            // cancel the scheduled notification.
            Context context = getApplicationContext();
            AlarmManager alarmManager =
                    (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent cancelServiceIntent = new Intent(context, Receiver.class);
            PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(context,
                    Receiver.SERVICE_ID, cancelServiceIntent,0);
            alarmManager.cancel(cancelServicePendingIntent);
        }

        // Update the view to reflect a checked circle (if needed)
        ibCheckin.setImageResource(R.drawable.check);
        checkInUser();
    }


    public void checkInUser() {

        // Create a new checkin object and save it in background
        final Checkin checkin = new Checkin();
        final Date newCheckinDate = new Date();
        checkin.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    checkin.saveInBackground();                         // Save to Parse
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                final User user = (User) ParseUser.getCurrentUser();
                                user.setLastCheckin(checkin);           // Update user's checkin.
                                user.saveInBackground();                // Save to Parse
                                // Format the dates nicely.
                                String formatedDate = DATE_FORMAT.format(newCheckinDate);
                                String newString = getRelativeTimeAgo(formatedDate);
                                formatedDate = getFormattedStringDate(formatedDate);
                                // Update the TV's
                                tvRelativeCheckinTime.setText(newString);
                                tvCheckinTime.setText(formatedDate);
                                isCheckedIn = isCheckedIn(newCheckinDate);
                                // Schedule the next notification.
                                int mins = (int) currentUser.getNumber("checkin");
                                scheduleNotification(getNotification(), mins * 60000);
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
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

    /*---------- Listener class to get coordinates -------------*/
    protected void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (isLocationEnabled(MainActivity.this)) {
            //retrieve criteria from device
            criteria = new Criteria();

            bestProvider = locationManager.getBestProvider(criteria, true);
            //You can still do this if you like, you might get lucky:
            Location location = locationManager.getLastKnownLocation(bestProvider);
            Location gps_location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location net_location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps_location != null) {
                Log.e("TAG", "GPS is on");
                saveLocation(gps_location);
            } else if (net_location != null) {
                saveLocation(net_location);
            } else if (location != null) {
                saveLocation(location);
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        } else {
            showGPSDisabledAlertToUser();
        }
    }


    @Override
    public void onLocationChanged(Location loc) {

//            //remove location callback:
//            locationManager.removeUpdates(this);
//
//
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
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }

    /**
     * This function creates a notification channel if necessary.
     */
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

    /**
     * Create a notification to go to Receiver class.
     * @return: the generated notification.
     */
    public Notification getNotification() {
        //Toast.makeText(context, "Going to receiver??", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, Receiver.class);
        intent.putExtra("actionName", "checkIn");
        intent.putExtra("user", currentUser);

        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Receiver.SERVICE_ID,
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
     * @param delay:        the delay at which to send the notification
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
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    /**
     * TODO -- ADD JAVADOC
     */
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
    public void onRefresh(View v) {
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
