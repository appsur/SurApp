package com.pusheenicorn.safetyapp;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Receiver extends WakefulBroadcastReceiver {

    User currentUser;
    Checkin checkin;
    Context mContext;
    public static final int SERVICE_ID = 10;
    private static final String CHANNEL_ID = "checkin";

    // Called automatically when the Broadcast Receiver is triggered.
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        int mins = 0;

        // Make sure that we are indeed receiving a checkin broadcast
        String action = intent.getStringExtra("actionName");
        if (action.equals("checkIn")) {
            // If so, register the user passed in from the intent
            // and extract values as needed. Schedule the next
            // checkin at the appropriate time based on what the
            // user's checkin frequency is.
            currentUser = intent.getParcelableExtra("user");
            mins = (int) currentUser.getNumber("checkin");
            scheduleNotification(getNotification(), mins * 60000);
            startCheckIn();
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    /**
     * This function checks whether a user is already checked in.
     */
    public void startCheckIn() {
        // Find time of last checkin by geting the checkin id and making a query.
        final String checkinId;

        // Get the user's most recent checkin id.
        checkinId = currentUser.getLastCheckin().getObjectId();

        // Make a query for the checkin by searching for the id.
        final Checkin.Query checkinQuery = new Checkin.Query();
        checkinQuery.getTop().whereEqualTo("objectId", checkinId);
        checkinQuery.findInBackground(new FindCallback<Checkin>() {
            @Override
            public void done(List<Checkin> objects, com.parse.ParseException e) {
                if (e == null) {
                    checkin = objects.get(0);
                    Date date = checkin.getCreatedAt();
                    if (!isChecked(date))
                    {
                        nowCheckin();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This function takes a date and determines whether the checkin made at that
     * date has expired or not.
     *
     * @param prevDate the date of the last check in
     * @return true if the current user is already checked in
     *         false if the current user is not checked in
     */
    public boolean isChecked(Date prevDate) {
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

    /**
     * This function performs the task of checking in if the user is not
     * already checked in.
     */
    public void nowCheckin()
    {
        // Create a new checkin.
        final Checkin checkin = new Checkin();
        checkin.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // Save the new checkin to Parse.
                    checkin.saveInBackground();
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                // Save the updated state to Parse.
                                final User user = (User) ParseUser.getCurrentUser();
                                user.setLastCheckin(checkin);
                                user.saveInBackground();

                                // If the main activity is already running, resume it.
                                if (Helper.isAppRunning(mContext,
                                        "com.pusheenicorn.safetyapp")) {
                                    Intent i = new Intent();
                                    i.setClassName("com.pusheenicorn.safetyapp",
                                            "com.pusheenicorn.safetyapp." +
                                                    "MainActivity");
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(i);
                                }
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
     * This function constructs a notification with an intent to fire the
     * Broadcast receiver.
     *
     * @return notification: the notification constructed
     */
    public Notification getNotification() {
        Intent intent = new Intent(mContext, Receiver.class);
        intent.putExtra("actionName", "checkIn");
        intent.putExtra("user", currentUser);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(mContext, CHANNEL_ID);
        builder.setContentTitle("Sûr");
        builder.setSmallIcon(R.drawable.check);
        builder.setContentText("Please remember to check in!");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        // builder.setContentIntent(pendingIntent);
        // builder.setAutoCancel(true);
        builder.addAction(R.drawable.check_outline, "Check in now!", pendingIntent);
        builder.setOngoing(true);
        return builder.build();
    }

    /**
     * Schedule a new notification at a given delay.
     *
     * @param notification: the notification to schedule
     * @param delay: the delay to send it at
     */
    public void scheduleNotification(Notification notification, int delay) {
        //Toast.makeText(this, "Scheduled notification in " + (delay / 60000)
        //+ " minutes", Toast.LENGTH_LONG).show();
        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

}
