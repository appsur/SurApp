package com.pusheenicorn.safetyapp.models;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.MapActivity;
import com.pusheenicorn.safetyapp.NotificationPublisher;
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.CheckinReceiver;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jared1158 on 7/25/18.
 */

public class FriendAlert {
    Context context;
    List<Friend> friends;
    User currentUser;
    User myFriend;
    NotificationManager notif;
    User primary;
    String their_id;
    String my_id;

    private static final String CHANNEL_ID = "checkin";




    private int timeSinceLastCheckin (Date prevDate, User user) {
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

        // true curr - truePrev is the number of minutes ago that the user checked in
        int trueCurr = (currDateInts[0] * 43800) + (currDateInts[1] * 1440)
                + (currDateInts[2] * 525600) + (currDateInts[3] * 60) + currDateInts[4];
        int truePrev = (prevDateInts[0] * 43800) + (prevDateInts[1] * 1440)
                + (prevDateInts[2] * 525600) + (prevDateInts[3] * 60) + prevDateInts[4];
        // threshold is the length of a user's checkin cycle
        //int threshold = (int) user.getNumber("checkin");

        return (trueCurr - truePrev);
    }

    public void alertNeeded(final Context context) {

        currentUser = (User) ParseUser.getCurrentUser();
        friends = currentUser.getFriends();
        notif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            my_id = currentUser.fetchIfNeeded().getObjectId();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //iterate through friends and check each friend's check in status
        for (final Friend fr : friends) {
            try {
                myFriend = (User) fr.fetchIfNeeded().getParseUser("user");
                primary = (User) myFriend.fetchIfNeeded().getParseUser("primaryContact");
                their_id = primary.fetchIfNeeded().getObjectId();

            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Toast.makeText(context, "My id is: " + my_id + "\n Their id is: " + their_id, Toast.LENGTH_LONG).show();
            if (my_id.equals(their_id)){
                Toast.makeText(context, "HELLLOOO", Toast.LENGTH_LONG).show();
                //assign friend's last checkin to a date object
                //Ishani----------------------------------------------------------------------------
                String checkinId = myFriend.getLastCheckin().getObjectId();

                // Query by checkinId
                final Checkin.Query checkinQuery = new Checkin.Query();
                checkinQuery.getTop().whereEqualTo("objectId", checkinId);
                checkinQuery.findInBackground(new FindCallback<Checkin>() {
                    @Override
                    public void done(List<Checkin> objects, ParseException e) {
                        if (e == null) {
                            // Get the checkin object and format its date
                            final Checkin checkin = objects.get(0);
                            Date date = checkin.getCreatedAt();
                            int placeholder = 1;
                            int cycle = (int) myFriend.getNumber("checkin");
                            int time = timeSinceLastCheckin(date,myFriend);
                            if (time > (placeholder * cycle) ){
                                NotificationUtil notificationUtil = new NotificationUtil(context, currentUser, fr);
                                notificationUtil.createNotificationChannel();
                                notificationUtil.scheduleNotification(notificationUtil.getReminderNotification(fr), 0);
                                //notif.notify(0, getNotification(currentUser , myFriend , context));
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }


    public Notification getNotification(User user, User friend , Context context) {
        //Toast.makeText(context, "Going to receiver??", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context.getApplicationContext(), MapActivity.class);
        intent.putExtra("actionName", "alert");
        intent.putExtra("user", currentUser);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(friend));

        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 10,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context , 0 , intent, 0);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                        .setContentTitle("Sûr")
                        .setSmallIcon(R.drawable.check)
                        .setContentText(user.getName() + " has not checked-in in a while")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.ic_person, "view" , pendingIntent)
                        .setOngoing(true);
        builder.setContentIntent(pendingIntent);
         builder.setAutoCancel(true);
        return builder.build();
    }




}
