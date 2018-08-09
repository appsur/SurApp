package com.pusheenicorn.safetyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.FriendAlert;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jared1158 on 7/31/18.
 */

public class FriendCheckReceiver extends BroadcastReceiver {
    Context mContext;
    FriendAlert alert;
    Event mCurrentEvent;
    User mCurrentUser;
    NotificationUtil notificationUtil;
    public int count = 0;
    public int MAX = 10;
    ArrayList<Event> events;

    @Override
    public void onReceive(Context context , Intent intent ) {
        //if (count < MAX) {
        mCurrentUser = (User) ParseUser.getCurrentUser();
        notificationUtil = new NotificationUtil(context, mCurrentUser);
        notificationUtil.createNotificationChannel();

        //Toast.makeText(context, "hello" + mCurrentUser, Toast.LENGTH_LONG).show();
        mContext = context;
        if (mCurrentUser != null) {
            friendsCheck();
            try {
                events = (ArrayList) mCurrentUser.fetchIfNeeded().getList("events");
                if (events != null && !events.isEmpty())
                {
                    receiveNotification();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mCurrentUser = (User) ParseUser.getCurrentUser();
        if (mCurrentUser != null) {
            friendsCheck();
        }
    }


    public void receiveNotification() {
        for (int i = 0; i < events.size(); i++)
        {
            Event event = events.get(i);
            event.sendNotifications(mCurrentUser.getObjectId(), notificationUtil, mContext,
                    (i * 7));
            String name = event.getName();
            // Toast.makeText(mContext, "hello " + i + event.getName(), Toast.LENGTH_LONG).show();
        }
    }



    public void friendsCheck(){
        alert = new FriendAlert();
        mCurrentUser = (User) ParseUser.getCurrentUser();
        alert.alertNeeded(mContext);
    }

    public void getEventNotifications(Event event) {
        List<Alert> alerts = event.getAlerts();

        if (alerts == null) {
            return;
        } else {
            for (int i = 0; i < alerts.size(); i++) {
                Alert curr = alerts.get(i);
                if (curr.getSeenBy() == null ||
                        !curr.getSeenBy().contains(mCurrentUser.getObjectId())) {
                    // Schedule a notification for this alert
                    try {
                        String message = event.getName() + " alert: " + curr.getMessage()
                                + "\n (from " +
                                curr.fetchIfNeeded().getString(Alert.KEY_USERNAME) + " )";

                        notificationUtil
                                .scheduleNotification(notificationUtil
                                        .getAlertNotification(message), 0);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Mark it as seen by this user
                    curr.addSeenBy(mCurrentUser.getObjectId());
                    // Save the alert state to Parse
                    try {
                        curr.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

