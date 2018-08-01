package com.pusheenicorn.safetyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver
{
    Event mCurrentEvent;
    User mCurrentUser;
    NotificationUtil notificationUtil;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getStringExtra(EventsActivity.SERVICE_KEY) == EventsActivity.ALERT_EVENT)
        {
            mCurrentEvent = intent.getParcelableExtra(EventsActivity.INTENT_EVENT_KEY);
            mCurrentUser = intent.getParcelableExtra(EventsActivity.INTENT_USER_KEY);
            notificationUtil = new NotificationUtil(context, mCurrentUser);
            getEmergencyNotifications();
        }
    }

    public void getEmergencyNotifications() {
        List<Alert> alerts = mCurrentEvent.getAlerts();

        if (alerts == null) {
            return;
        } else {
            for (int i = 0; i < alerts.size(); i++) {
                Alert curr = alerts.get(i);
                if (curr.getSeenBy() == null ||
                        !curr.getSeenBy().contains(mCurrentUser.getObjectId())) {
                    // Schedule a notification for this alert
                    try {
                        String message = curr.fetchIfNeeded().getString(Alert.KEY_USERNAME) +
                                " says: " + curr.getMessage();
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