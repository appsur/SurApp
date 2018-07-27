package com.pusheenicorn.safetyapp.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.pusheenicorn.safetyapp.CheckinReceiver;
import com.pusheenicorn.safetyapp.MainActivity;
import com.pusheenicorn.safetyapp.NotificationPublisher;
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.models.User;

public class NotificationUtil {

    Context mContext;
    User mCurrentUser;

    public NotificationUtil(Context context, User currentUser) {
        mContext = context;
        mCurrentUser = currentUser;
    }

    /**
     * This function creates a notification channel if necessary.
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(MainActivity.CHANNEL_ID,
                    name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =
                    mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * This function takes a notification and delay and schedules the notification at current
     * time + delay.
     *
     * @param notification: the notification to be sent
     * @param delay:        the delay at which to send the notification
     */
    public void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(mContext, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    /**
     * Create a notification to go to CheckinReceiver class.
     * @return: the generated notification.
     */
    public Notification getNotification() {
        Intent intent = new Intent(mContext, CheckinReceiver.class);
        intent.putExtra("actionName", "checkIn");
        intent.putExtra("user", mCurrentUser);

        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, CheckinReceiver.SERVICE_ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(mContext,
                        MainActivity.CHANNEL_ID)
                        .setContentTitle(CheckinReceiver.APP_NAME)
                        .setSmallIcon(R.drawable.check)
                        .setContentText(CheckinReceiver.NOTIFICATION_MESSAGE)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.check_outline, CheckinReceiver.ACTION_MESSAGE, pendingIntent)
                        .setOngoing(true);
        // builder.setContentIntent(pendingIntent);
        // builder.setAutoCancel(true);
        return builder.build();
    }

    public void cancelCheckinNotification() {
        AlarmManager alarmManager =
                (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent cancelServiceIntent = new Intent(mContext, CheckinReceiver.class);
        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(mContext,
                CheckinReceiver.SERVICE_ID, cancelServiceIntent,0);
        alarmManager.cancel(cancelServicePendingIntent);
    }

}
