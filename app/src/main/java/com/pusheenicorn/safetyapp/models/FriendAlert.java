package com.pusheenicorn.safetyapp.models;

import android.app.NotificationManager;
import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by jared1158 on 7/25/18.
 */

public class FriendAlert {
    public static final int MONTHS_TO_SECONDS = 43800;
    public static final int DAYS_TO_SECONDS = 1440;
    public static final int YEARS_TO_SECONDS = 525600;
    public static final int MINS_TO_SECONDS = 60;
    Context context;
    List<Friend> friends;
    User currentUser;
    User myFriend;
    NotificationManager notif;
    User primary;
    String their_id;
    String my_id;
    int cycle;

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
        int trueCurr = (currDateInts[0] * MONTHS_TO_SECONDS) + (currDateInts[1] * DAYS_TO_SECONDS)
                + (currDateInts[2] * YEARS_TO_SECONDS) + (currDateInts[3] * MINS_TO_SECONDS) + currDateInts[4];
        int truePrev = (prevDateInts[0] * MONTHS_TO_SECONDS) + (prevDateInts[1] * DAYS_TO_SECONDS)
                + (prevDateInts[2] * YEARS_TO_SECONDS) + (prevDateInts[3] * MINS_TO_SECONDS) + prevDateInts[4];
        // threshold is the length of a user's checkin cycle

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
        if (friends != null && !friends.isEmpty()) {

            //iterate through friends and check each friend's check in status
            for (final Friend fr : friends) {
                try {
                    myFriend = (User) fr.fetchIfNeeded().getParseUser("user");
                    primary = (User) myFriend.fetchIfNeeded().getParseUser("primaryContact");
                    their_id = primary.fetchIfNeeded().getObjectId();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (my_id.equals(their_id)) {
                    //assign friend's last checkin to a date object
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
                                cycle = (int) myFriend.getNumber("checkin");
                                int missed = (int) myFriend.getNotificationThreshold();
                                int time = timeSinceLastCheckin(date, myFriend);
                                if (time > (missed * cycle)) {
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
    }
}
