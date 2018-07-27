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
import com.pusheenicorn.safetyapp.utils.CheckinUtil;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckinReceiver extends WakefulBroadcastReceiver {

    User currentUser;
    Checkin checkin;
    Context mContext;
    public static final int SERVICE_ID = 10;
    private static final String CHANNEL_ID = "checkin";
    private static final String ACTION_NAME = "actionName";
    private static final String CHECKIN_ACTION_ID = "checkIn";
    private static final String USER_KEY = "user";
    public static final String CHECKIN_FREQUENCY_KEY = "checkin";
    public static final int SECOND_TO_MILLIS = 60000;
    public static final String OBJECT_ID_KEY = "objectId";
    public static final int MONTHS_TO_SECONDS = 43800;
    public static final int DAYS_TO_SECONDS = 1440;
    public static final int YEARS_TO_SECONDS = 525600;
    public static final int MINS_TO_SECONDS = 60;
    public static final String PACKAGE_NAME = "com.pusheenicorn.safetyapp";
    public static final String MAIN_CLASS_NAME = "com.pusheenicorn.safetyapp.MainActivity";
    public static final String APP_NAME = "Sûr";
    public static final String NOTIFICATION_MESSAGE = "Please remember to check in!";
    public static final String ACTION_MESSAGE = "Check in now!";
    public static final DateFormat EASY_FORMAT = new SimpleDateFormat("MM/dd/yy/HH/mm");
    private NotificationUtil notificationUtil;
    private CheckinUtil checkinUtil;


    // Called automatically when the Broadcast CheckinReceiver is triggered.
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        int mins = 0;

        // Make sure that we are indeed receiving a checkin broadcast
        String action = intent.getStringExtra(ACTION_NAME);
        if (action.equals(CHECKIN_ACTION_ID)) {
            // If so, register the user passed in from the intent and extract values as
            // needed. Schedule the next checkin at the appropriate time based on what the
            // user's checkin frequency is.
            currentUser = intent.getParcelableExtra(USER_KEY);
            notificationUtil = new NotificationUtil(context, currentUser);
            checkinUtil = new CheckinUtil(context, currentUser);
            mins = (int) currentUser.getNumber(CHECKIN_FREQUENCY_KEY);
            notificationUtil.scheduleNotification(
                    notificationUtil.getNotification(), mins * SECOND_TO_MILLIS);
            checkinUtil.startCheckIn();
        }

        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }
}
