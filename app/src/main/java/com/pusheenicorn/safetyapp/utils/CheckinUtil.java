package com.pusheenicorn.safetyapp.utils;

import android.content.Context;
import android.content.Intent;

import com.parse.FindCallback;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.receivers.CheckinReceiver;
import com.pusheenicorn.safetyapp.StatusHelper;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.User;

import java.util.Date;
import java.util.List;

public class CheckinUtil {

    User mCurrentUser;
    Context mContext;
    Checkin checkin;

    public CheckinUtil(Context context, User currentUser) {
        mCurrentUser = currentUser;
        mContext = context;
    }


    /**
     * This function takes a date and determines whether the checkin made at that
     * date has expired or not.
     *
     * @param prevDate the date of the last check in
     * @return true if the current user is already checked in
     *         false if the current user is not checked in
     */
    public boolean isChecked(Date prevDate) {

        // Get current Date.
        Date currDate = new Date();

        // Split by regex "/" convert to int array and find time difference.
        String[] currDateArr = CheckinReceiver.EASY_FORMAT.format(currDate).split("/");
        String[] prevDateArr = CheckinReceiver.EASY_FORMAT.format(prevDate).split("/");
        int[] currDateInts = new int[5];
        int[] prevDateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            currDateInts[i] = Integer.parseInt(currDateArr[i]);
            prevDateInts[i] = Integer.parseInt(prevDateArr[i]);
        }
        int trueCurr = (currDateInts[0] * CheckinReceiver.MONTHS_TO_SECONDS) + (currDateInts[1]
                * CheckinReceiver.DAYS_TO_SECONDS)
                + (currDateInts[2] * CheckinReceiver.YEARS_TO_SECONDS) + (currDateInts[3]
                * CheckinReceiver.MINS_TO_SECONDS)
                + currDateInts[4];
        int truPrev = (prevDateInts[0] * CheckinReceiver.MONTHS_TO_SECONDS) + (prevDateInts[1]
                * CheckinReceiver.DAYS_TO_SECONDS)
                + (prevDateInts[2] * CheckinReceiver.YEARS_TO_SECONDS) + (prevDateInts[3]
                * CheckinReceiver.MINS_TO_SECONDS)
                + prevDateInts[4];
        int threshold = (int) mCurrentUser.getNumber(CheckinReceiver.CHECKIN_FREQUENCY_KEY);

        if (trueCurr - truPrev > threshold) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This function checks whether a user is already checked in.
     */
    public void startCheckIn() {
        // Find time of last checkin by geting the checkin id and making a query.
        final String checkinId;

        // Get the user's most recent checkin id.
        checkinId = mCurrentUser.getLastCheckin().getObjectId();

        // Make a query for the checkin by searching for the id.
        final Checkin.Query checkinQuery = new Checkin.Query();
        checkinQuery.getTop().whereEqualTo(CheckinReceiver.OBJECT_ID_KEY, checkinId);
        checkinQuery.findInBackground(new FindCallback<Checkin>() {
            @Override
            public void done(List<Checkin> objects, com.parse.ParseException e) {
                if (e == null) {
                    checkin = objects.get(0);
                    Date date = checkin.getCreatedAt();
                    if (!isChecked(date))
                    {
                        nowCheckin();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This function performs the task of checking in if the user is not
     * already checked in.
     */
    public void nowCheckin()
    {
        // Create a new checkin.
        final Checkin checkin = new Checkin();
        checkin.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    // Save the new checkin to Parse.
                    checkin.saveInBackground();
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                // Save the updated state to Parse.
                                final User user = (User) ParseUser.getCurrentUser();
                                user.setLastCheckin(checkin);
                                user.saveInBackground();

                                // If the main activity is already running, resume it.
                                if (StatusHelper.isAppRunning(mContext,
                                        CheckinReceiver.PACKAGE_NAME)) {
                                    Intent i = new Intent();
                                    i.setClassName(CheckinReceiver.PACKAGE_NAME,
                                            CheckinReceiver.MAIN_CLASS_NAME);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(i);
                                }
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
