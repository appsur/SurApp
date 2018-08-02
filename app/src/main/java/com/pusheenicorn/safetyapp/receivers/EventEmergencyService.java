package com.pusheenicorn.safetyapp.receivers;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.parse.ParseException;
import com.pusheenicorn.safetyapp.EventsActivity;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.util.List;

public class EventEmergencyService extends IntentService {

    Event mCurrentEvent;
    User mCurrentUser;
    NotificationUtil notificationUtil;
    Context mContext;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public EventEmergencyService() {
        super("DisplayEmergencyAlert");
        mContext = this;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(mContext, "Hello", Toast.LENGTH_LONG).show();
        if (intent.getStringExtra(EventsActivity.SERVICE_KEY) == EventsActivity.ALERT_EVENT)
        {
            mCurrentEvent = intent.getParcelableExtra(EventsActivity.INTENT_EVENT_KEY);
            mCurrentUser = intent.getParcelableExtra(EventsActivity.INTENT_USER_KEY);
            notificationUtil = new NotificationUtil(mContext, mCurrentUser);
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
