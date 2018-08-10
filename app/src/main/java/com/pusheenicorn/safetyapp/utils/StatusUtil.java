package com.pusheenicorn.safetyapp.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * This class is a helper class that determines whether a specific activity is currently running
 * in foreground.
 */
public class StatusUtil {

    /**
     * This function determines whether an activity is running in the foreground.
     * @param context: the context of the activity
     * @param packageName: the package in which to find it
     * @return true if the activity is running, otherwise false
     */
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos =
                activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
