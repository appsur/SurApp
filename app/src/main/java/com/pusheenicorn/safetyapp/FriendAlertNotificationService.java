package com.pusheenicorn.safetyapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.FriendAlert;
import com.pusheenicorn.safetyapp.models.User;

import static com.pusheenicorn.safetyapp.EventsActivity.TAG;

/**
 * Created by jared1158 on 7/31/18.
 */

public class FriendAlertNotificationService extends IntentService {

    public FriendAlert alert;
    User user;



    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public FriendAlertNotificationService() {
        super("FriendAlertNotificationService");
    }








    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }





    public void friendsCheck(){
        alert = new FriendAlert();
        user = (User) ParseUser.getCurrentUser();
        alert.alertNeeded(getApplicationContext());
    }
}
