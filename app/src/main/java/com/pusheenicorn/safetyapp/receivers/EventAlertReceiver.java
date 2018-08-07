package com.pusheenicorn.safetyapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

public class EventAlertReceiver extends BroadcastReceiver
{
    Event mCurrentEvent;
    User mCurrentUser;
    Context mContext;
    NotificationUtil notificationUtil;


    public void onReceive(Context context, Intent intent)
    {
        // Toast.makeText(context, "running", Toast.LENGTH_SHORT).show();
        mCurrentUser = (User) ParseUser.getCurrentUser();
        notificationUtil = new NotificationUtil(context, mCurrentUser);
        mContext = context;
        getNotifications();
    }

    public void getNotifications()
    {
        if (mCurrentUser != null && mCurrentUser.getEvents() != null && !mCurrentUser.getEvents().isEmpty())
        {
            ArrayList<Event> events = (ArrayList) mCurrentUser.getEvents();
            for (int i = 0; i < events.size(); i++)
            {
                Event event = events.get(i);
                getEventNotifications(event);
            }
        }
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
                        String message = mCurrentEvent.getName() + " alert: " + curr.getMessage()
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