package com.pusheenicorn.safetyapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.FriendAlert;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.util.ArrayList;

/**
 * This is a receiver class for a broadcast intent that is fired every minute upon opening of
 * main activity. It handles event alerts and friend checkin reminders.
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

    /**
     * This function is executed whenever the broadcast is fired.
     * @param context- the context from which the intent was fired
     * @param intent- the intent attached to it
     */
    @Override
    public void onReceive(Context context , Intent intent ) {
        mCurrentUser = (User) ParseUser.getCurrentUser();
        notificationUtil = new NotificationUtil(context, mCurrentUser);
        notificationUtil.createNotificationChannel();
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

    /**
     * For each event that the user is part of, check for any event alerts.
     */
    public void receiveNotification() {
        for (int i = 0; i < events.size(); i++)
        {
            Event event = events.get(i);
            event.sendNotifications(mCurrentUser.getObjectId(), notificationUtil, mContext,
                    (i * 7));
            String name = event.getName();
        }
    }

    /**
     * Check on every friend.
     */
    public void friendsCheck(){
        alert = new FriendAlert();
        mCurrentUser = (User) ParseUser.getCurrentUser();
        alert.alertNeeded(mContext);
    }
}

