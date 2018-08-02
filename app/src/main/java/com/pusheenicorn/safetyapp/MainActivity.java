package com.pusheenicorn.safetyapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.FriendAlert;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.receivers.CheckinReceiver;
import com.pusheenicorn.safetyapp.utils.CheckinUtil;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity implements LocationListener {

    // Declare views
    private BottomNavigationView bottomNavigationView;
    private ImageButton ibEvents;
    private ImageButton ibProfileImage;
    private ImageButton ibCheckin;
    private ImageButton ibAddEvent;
    private ImageButton ibConfirmEvent;
    private TextView tvName;
    private TextView tvUsername;
    private TextView tvCheckinTime;
    private TextView tvRelativeCheckinTime;
    private TextView tvUpcomingActivities;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etEventName;
    private EditText etEventLocation;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private EditText startTime;
    private EditText endTime;
    private Button btnAM;
    private Button btnPM;
    private Button btnAM2;
    private Button btnPM2;
    private Button btnSelectStart;
    private Button btnSelectEnd;
    ImageButton ibVideo;

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
    public FriendAlert alert;

    // Declare adapter, events list, and events adapter
    // Declare adapter, events list, and events adapter.
    private EventAdapter eventAdapter;
    private ArrayList<Event> events;
    private RecyclerView rvEvents;
    // Define channel id for checkin notifications.
    public static final String CHANNEL_ID = "checkin";
    // Define some simple date formats.
    public static final String TWITTER_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
    public static final String NEAT_FORMAT = "%s %s %s:%s:00 %s 20%s%s";
    // Notification channel for this class
    NotificationUtil notificationUtil;
    CheckinUtil checkinUtil;


    // Variables for saving state between restarts
    public static String startString;
    public static String endString;
    public static String nameString;
    public static String locationString;
    public static String startTimeString;
    public static String endTimeString;
    public static boolean isAMStart;
    public static boolean isAMEnd;
    public static int monthStart;
    public static int monthEnd;
    public static String LIGHT_GRAY = "#F5F5F5";
    public static String TEAL = "#66C7CB";
    public static String PAST_ID = "ago";
    public static String FROM_CALENDAR = "fromCalendar";
    public static String START_KEY = "start";
    public static String END_KEY = "end";
    public static String DATE_KEY = "date";
    public static String BLACK = "#000000";
    private static String END_TIME_KEY = "endTime";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissionsPlease();                           // Check for geotracking permissions.
        context = this;                                     // Store the context for ease of access.
        currentUser = (User) ParseUser.getCurrentUser();    // Get the current user.

        // Create a notification util instance to send all notifications through
        notificationUtil = new NotificationUtil(context, currentUser);
        notificationUtil.createNotificationChannel();

        // Create a checkin util instance to do some checkin work externally
        checkinUtil = new CheckinUtil(context, currentUser);

        isCheckedIn = false;                                // Set default values
        setEventsAdapter();                                 // Set up the events adapter.
        ibVideo = findViewById(R.id.ibVideo);
        ibVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goVideo = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(goVideo);
            }
        });
        // Set up the BNV.
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        setNavigationDestinations(MainActivity.this, bottomNavigationView);
        initializeViews();                                  // Initialize the views.
        populateEvents();                                   // Populate the events recycler view
        getLocation();                                      // Get the user's location.
        initializeNavItems(mNavItems);                      // Initialize navigation items
        setUpDrawerLayout();                                // Set up the pull-out menu.
        friendsCheck();                                     // Check if friends need to check-in
        setNewInvisible();                                  // Hide all edit views

        Intent incoming = getIntent();
        boolean fromCalendar = incoming.getBooleanExtra(FROM_CALENDAR, false);

        if (fromCalendar)
        {
            boolean start = incoming.getBooleanExtra(START_KEY, false);
            boolean end = incoming.getBooleanExtra(END_KEY, false);

            String date = incoming.getStringExtra(DATE_KEY);
            if (start)
            {
                tvStartDate.setTextColor(Color.parseColor(BLACK));
                tvStartDate.setText(date);
                startString = date;
                String[] dateArr = date.split(" ");
                monthStart = Integer.parseInt(dateArr[1].split("/")[0]);
                setNewVisible();
                // Restore previous state
                if (endString != null)
                {
                    tvEndDate.setTextColor(Color.parseColor(BLACK));
                    tvEndDate.setText(endString);
                }
                restoreState();
            }

            else if (end)
            {
                tvEndDate.setTextColor(Color.parseColor("#000000"));
                tvEndDate.setText(date);
                endString = date;
                String[] dateArr = date.split(" ");
                monthEnd = Integer.parseInt(dateArr[1].split("/")[0]);
                setNewVisible();
                // Restore previous state
                if (startString != null)
                {
                    tvStartDate.setTextColor(Color.parseColor("#000000"));
                    tvStartDate.setText(startString);
                }
                restoreState();

                setNewVisible();
            }
        }
        onResume();
    }

    public void restoreState() {

        if (nameString != null)
        {
            etEventName.setText(nameString);
        }
        if (locationString != null)
        {
            etEventLocation.setText(locationString);
        }
        if (startTimeString != null)
        {
            etStartTime.setText(startTimeString);
        }
        if (endTimeString != null)
        {
            etEndTime.setText(endTimeString);
        }
        if (isAMStart)
        {
            btnAM2.setBackgroundColor(Color.parseColor(TEAL));
            btnPM2.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
        }
        else {
            btnAM2.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
            btnPM2.setBackgroundColor(Color.parseColor(TEAL));
        }
        if (isAMEnd)
        {
            btnAM.setBackgroundColor(Color.parseColor(TEAL));
            btnPM.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
        }
        else {
            btnAM.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
            btnPM.setBackgroundColor(Color.parseColor(TEAL));
        }
    }

    public void setNewVisible() {
        // Date et's
        tvStartDate.setVisibility(View.VISIBLE);
        tvEndDate.setVisibility(View.VISIBLE);
        // Title
        tvUpcomingActivities.setText("NEW EVENT");
        // Time et's
        etEndTime.setVisibility(View.VISIBLE);
        etStartTime.setVisibility(View.VISIBLE);
        // Location and Name
        etEventLocation.setVisibility(View.VISIBLE);
        etEventName.setVisibility(View.VISIBLE);
        // Switch from icon for adding event to confirming event
        ibAddEvent.setVisibility(View.INVISIBLE);
        ibConfirmEvent.setVisibility(View.VISIBLE);
        // Hide rv
        rvEvents.setVisibility(View.INVISIBLE);
        btnSelectStart.setVisibility(View.VISIBLE);
        btnSelectEnd.setVisibility(View.VISIBLE);
        btnAM.setVisibility(View.VISIBLE);
        btnPM.setVisibility(View.VISIBLE);
        btnAM2.setVisibility(View.VISIBLE);
        btnPM2.setVisibility(View.VISIBLE);
    }

    public void setNewInvisible() {
        // Date et's
        tvStartDate.setVisibility(View.INVISIBLE);
        tvEndDate.setVisibility(View.INVISIBLE);
        // Title
        tvUpcomingActivities.setText("UPCOMING ACTIVITIES");
        // Time et's
        etEndTime.setVisibility(View.INVISIBLE);
        etStartTime.setVisibility(View.INVISIBLE);
        // Location and Name
        etEventLocation.setVisibility(View.INVISIBLE);
        etEventName.setVisibility(View.INVISIBLE);
        // Switch from icon for adding event to confirming event
        ibAddEvent.setVisibility(View.VISIBLE);
        ibConfirmEvent.setVisibility(View.INVISIBLE);
        // Hide rv
        rvEvents.setVisibility(View.VISIBLE);
        btnSelectStart.setVisibility(View.INVISIBLE);
        btnSelectEnd.setVisibility(View.INVISIBLE);
        btnAM.setVisibility(View.INVISIBLE);
        btnPM.setVisibility(View.INVISIBLE);
        btnAM2.setVisibility(View.INVISIBLE);
        btnPM2.setVisibility(View.INVISIBLE);
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
//        Toast.makeText(MainActivity.this, latitude + ":"
//                + longitude, Toast.LENGTH_LONG).show();
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
        checkinQuery.getTop().whereEqualTo(Checkin.OBJECT_ID_KEY, checkinId);
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
                    isCheckedIn = checkinUtil.isChecked(date);
                    if (isCheckedIn) {
                        ibCheckin.setImageResource(R.drawable.check);
                    } else {
                        ibCheckin.setImageResource(R.drawable.check_outline);
                        // Send an immediate notification.
                        notificationUtil.scheduleNotification(
                                notificationUtil.getNotification(), 0);
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
        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvEndDate = (TextView) findViewById(R.id.tvEndDate);
        btnSelectStart = (Button) findViewById(R.id.btnSelectStart);
        btnSelectEnd = (Button) findViewById(R.id.btnSelectEnd);
        btnAM = (Button) findViewById(R.id.btnAM);
        btnPM = (Button) findViewById(R.id.btnPM);
        btnAM2 = (Button) findViewById(R.id.btnAM2);
        btnPM2 = (Button) findViewById(R.id.btnPM2);

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



    public void friendsCheck(){
        alert = new FriendAlert();
        currentUser = (User) ParseUser.getCurrentUser();
        alert.alertNeeded(context);
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
        // Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show();
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
                    removeExpiredEvents();
                    sortEvents();
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
            notificationUtil.cancelCheckinNotification();
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
                                isCheckedIn = checkinUtil.isChecked(newCheckinDate);
                                // Schedule the next notification.
                                int mins = (int) currentUser.getNumber("checkin");
                                notificationUtil.scheduleNotification(
                                        notificationUtil.getNotification(), mins *
                                        CheckinReceiver.SECOND_TO_MILLIS);
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

    /*---------- Listener class to get coordinates -------------*/
    protected void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (isLocationEnabled(MainActivity.this)) {
            //retrieve criteria from device
            criteria = new Criteria();

            bestProvider = locationManager.getBestProvider(criteria, true);
            //You can still do this if you like, you might get lucky:
            Location location = locationManager.getLastKnownLocation(bestProvider);
            Location gps_location =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location net_location =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps_location != null) {
                Log.e("TAG", "GPS is on");
                saveLocation(gps_location);
            } else if (net_location != null) {
                saveLocation(net_location);
            } else if (location != null) {
                saveLocation(location);
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider,
                        1000, 0, this);
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
//            //Toast.makeText(MainActivity.this, "latitude:" + latitude + " longitude:"
// + longitude, Toast.LENGTH_SHORT).show();
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
        setNewVisible();
    }

    public void onConfirmEvent(View view) {
        final Event event = new Event();
        // Set the event attributes and save in background
        event.setName(etEventName.getText().toString());
        event.setLocation(etEventLocation.getText().toString());

        String startTotalTime = retrievePrettyDate(tvStartDate.getText().toString(),
                etStartTime.getText().toString(), true);
        String endTotalTime = retrievePrettyDate(tvEndDate.getText().toString(),
               etEndTime.getText().toString(), false);
        event.setStart(startTotalTime);
        event.setEnd(endTotalTime);
        event.addUser(currentUser);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // Add the event to the events list, notify the adapter, and update views
                    eventAdapter.clear();
                    populateEvents();
                    setNewInvisible();
                    // Add to the user's events
                    final User thisUser = (User) ParseUser.getCurrentUser();
                    thisUser.addEvent(event);
                    thisUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            thisUser.saveInBackground();
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // Save the current static variables
    public void saveState() {
        nameString = etEventName.getText().toString();
        locationString = etEventLocation.getText().toString();
        startTimeString = etStartTime.getText().toString();
        endTimeString = etEndTime.getText().toString();
    }

    public void onSelectStart(View view) {
        Intent intent = new Intent(MainActivity.this, CalendarChoiceActivity.class);
        intent.putExtra("start", true);
        if (tvEndDate.getText() == null)
        {
            endString = null;
        }
        else {
            endString = tvEndDate.getText().toString();
        }
        saveState();
        startActivity(intent);
    }

    public void onSelectEnd(View view) {
        Intent intent = new Intent(MainActivity.this, CalendarChoiceActivity.class);
        intent.putExtra("end", true);
        if (tvStartDate.getText() == null)
        {
            startString = null;
        }
        else {
            startString = tvStartDate.getText().toString();
        }
        saveState();
        startActivity(intent);
    }

    public void onAM(View view) {
        btnAM.setBackgroundColor(Color.parseColor(TEAL));
        btnPM.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
        isAMEnd = true;
    }

    public void onPM(View view) {
        btnPM.setBackgroundColor(Color.parseColor(TEAL));
        btnAM.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
        isAMEnd = false;
    }

    public void onPM2(View view) {
        btnPM2.setBackgroundColor(Color.parseColor(TEAL));
        btnAM2.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
        isAMStart = false;
    }

    public void onAM2(View view) {
        btnAM2.setBackgroundColor(Color.parseColor(TEAL));
        btnPM2.setBackgroundColor(Color.parseColor(LIGHT_GRAY));
        isAMStart = true;
    }

    public String retrievePrettyDate(String date, String time, boolean startValue) {
        String[] dateVals = date.split(" ");
        String[] dateRaw = dateVals[1].split("/");
        String ret = "";
        ret = dateVals[0] + " " + getPrettyMonth(dateRaw[0]) + " "
                + getPrettyDay(dateRaw[1]) + " "
                + getMilitaryTime(time, startValue) + ":00 "
                + dateVals[dateVals.length - 1] + " "
                + getPrettyYear(dateRaw[2]) + " " ;
        return ret;
    }

    public String getMilitaryTime(String time, boolean startValue){
        String milTime = time;
        String[] timeArr = time.split(":");
        if ((startValue && !isAMStart) || (!startValue && !isAMEnd))
        {
            int timeInt = Integer.parseInt(timeArr[0]) + 12;
            milTime = timeInt + ":" + timeArr[1];
        }
        return milTime;
    }

    public String getPrettyYear(String year)
    {
        String prettyYear = year;
        if (year.length() < 4)
        {
           prettyYear = "20" + year;
        }
        return prettyYear;
    }

    public String getPrettyDay(String day)
    {
        String prettyDay = day;
        if (day.length() < 2)
        {
            prettyDay = "0" + day;
        }
        return prettyDay;
    }

    public String getPrettyMonth(String month) {
        String prettyMonth = month;
        switch(month)
        {
            case "1":
                month = "JAN";
                break;
            case "2":
                month = "FEB";
                break;
            case "3":
                month = "MAR";
                break;
            case "4":
                month = "APR";
                break;
            case "5":
                month = "MAY";
                break;
            case "6":
                month = "JUN";
                break;
            case "7":
                month = "JUL";
                break;
            case "8":
                month = "AUG";
                break;
            case "9":
                month = "SEP";
                break;
            case "10":
                month = "OCT";
                break;
            case "11":
                month = "NOV";
                break;
            case "12":
                month = "DEC";
                break;
        }
        return month;
    }


    // Once an event has expired, remove it from the list.
    public void removeExpiredEvents() {
        ArrayList<Event> eventsToRemove = new ArrayList<Event>();

        for (int i = 0; i < events.size(); i++)
        {
            Event event = events.get(i);
            if (isExpired(event))
            {
                eventsToRemove.add(event);
            }
        }

        while(!eventsToRemove.isEmpty())
        {
            events.remove(eventsToRemove.get(0));
            eventsToRemove.remove(0);
        }
        eventAdapter.notifyDataSetChanged();
    }

    public boolean isExpired(Event event)
    {
        String end = "";
        try {
            end = getRelativeTimeAgo(event.fetchIfNeeded().getString(END_TIME_KEY));
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        if (end.contains(PAST_ID) || isExpiredEvent(event)){
            return true;
        }

        return false;
    }

    public boolean isExpiredEvent (Event event) {

        // Define format type.
        DateFormat df = new SimpleDateFormat("MM/dd/yy/HH/mm");

        // Get current Date.
        Date currDate = new Date();

        // Split by regex "/" convert to int array and find time difference.
        String[] currDateArr = df.format(currDate).split("/");
        String[] endDatePreArr = new String[8];
        try {
            endDatePreArr = event.fetchIfNeeded().getString("endTime").split(" |:");
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

        String year = endDatePreArr[7].substring(2, 4);

        String month = getValMonth(endDatePreArr[1]) + "";

        String[] endDateArr = {month, endDatePreArr[2], year,
                endDatePreArr[3],
                endDatePreArr[4]};
        int[] currDateInts = new int[5];
        int[] endDateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            currDateInts[i] = Integer.parseInt(currDateArr[i]);
            endDateInts[i] = Integer.parseInt(endDateArr[i]);
        }

        int trueCurr = (currDateInts[0] * 43800) + (currDateInts[1] * 1440)
                + (currDateInts[2] * 525600) + (currDateInts[3] * 60) + currDateInts[4];
        int trueEnd = (endDateInts[0] * 43800) + (endDateInts[1] * 1440)
                + (endDateInts[2] * 525600) + (endDateInts[3] * 60) + endDateInts[4];

        // If the current time is greater than the end time, the event has expired.
        if (trueCurr > trueEnd) {
            return true;
        }

        return false;
    }

    public void sortEvents() {
        Collections.sort(events);
    }

    public int getValMonth(String month) {
        int monthID = 0;
        switch(month) {
            case ("JAN"):
                return 1;
            case ("FEB"):
                return 2;
            case ("MAR"):
                return 3;
            case ("APR"):
                return 4;
            case ("MAY"):
                return 5;
            case ("JUN"):
                return 6;
            case ("JUL"):
                return 7;
            case ("AUG"):
                return 8;
            case ("SEP"):
                return 9;
            case ("OCT"):
                return 10;
            case ("NOV"):
                return 11;
            case ("DEC"):
                return 12;
        }

        return monthID;
    }
}
