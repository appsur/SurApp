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
    boolean isCheckedIn;
    Context mContext;
    private static final String CHANNEL_ID = "checkin";

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        int mins = 0;

        String action = intent.getStringExtra("actionName");
        if (action.equals("checkIn")) {
            currentUser = intent.getParcelableExtra("user");
            mins = (int) currentUser.getNumber("checkin");
            scheduleNotification(getNotification(), mins * 60000);
            startCheckIn();
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void startCheckIn() {
        // Find time of last checkin by geting checkin id and making query.
        final String checkinId;
        isCheckedIn = false;
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
                    String[] formatedDateArr = formatedDate.split(" ");
                    formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] + " " +
                            formatedDateArr[2] +
                            " " + formatedDateArr[3];
                    nowCheckin();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void nowCheckin()
    {
        final Checkin checkin;
        final Date newCheckinDate;

        if (!isCheckedIn ){
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
                                    user.setName("Gracoo");
                                    user.saveInBackground();
                                    if (Helper.isAppRunning(mContext, "com.pusheenicorn.safetyapp")) {
                                        Intent i = new Intent();
                                        i.setClassName("com.pusheenicorn.safetyapp","com.pusheenicorn.safetyapp.MainActivity");
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

            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
            String formatedDate = dateFormat.format(newCheckinDate);
            String[] formatedDateArr = formatedDate.split(" ");
            formatedDate = formatedDateArr[0] + " " + formatedDateArr[1] + " " +
                    formatedDateArr[2] + " " + formatedDateArr[3];

            int mins = (int) currentUser.getNumber("checkin");
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
        builder.addAction(R.drawable.ic_vector_heart, "Check in now", pendingIntent);
        builder.setOngoing(true);
        return builder.build();
    }

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
