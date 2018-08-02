package com.pusheenicorn.safetyapp.models;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pusheenicorn.safetyapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Event")
public class Event extends ParseObject implements Comparable<Event> {
    private final static String KEY_NAME = "name";
    private final static String KEY_USERS = "usersAttending";
    private final static String KEY_START = "startTime";
    private final static String KEY_END = "endTime";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_BANNER_IMAGE = "bannerimage";
    private final static String KEY_ALERTS = "alerts";

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public ParseFile getBannerImage() {
        return getParseFile(KEY_BANNER_IMAGE);
    }

    public void setBannerImage(ParseFile parseFile) {
        put(KEY_BANNER_IMAGE, parseFile);
    }

    public String getStart() {
        return getString(KEY_START);
    }

    public void setStart(String start) {
        put(KEY_START, start);
    }

    public String getEnd() {
        return getString(KEY_END);
    }

    public void setEnd(String end) {
        put(KEY_END, end);
    }

    public List<User> getUsers() {
        return getList(KEY_USERS);
    }

    public List<Alert> getAlerts() {
        return getList(KEY_ALERTS);
    }

    public void addAlert(Alert alert) {
        add(KEY_ALERTS, alert);
    }

    public ArrayList<String> getUsersIds() {
        List<User> myUsers = getUsers();
        ArrayList<String> myUsersIds = new ArrayList<String>();
        for (int i = 0; i < myUsers.size(); i++) {
            myUsersIds.add(myUsers.get(i).getObjectId());
        }
        return myUsersIds;
    }

    public void addUser(User user) {
        add(KEY_USERS, user);
    }

    @Override
    public int compareTo(@NonNull Event otherEvent) {
        String thisStart = "";
        String otherStart = "";
        String val1 = "";
        String val2 = "";
        try {
            thisStart = this.fetchIfNeeded().getString("startTime");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            otherStart = otherEvent.fetchIfNeeded().getString("startTime");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // If this event starts before the other, then the absolute time of this
        // event should be less than that of the other, so we return < 0.
        // If this event starts after the other, then the absolute time of this
        // event should be greater than that of the other, so we return > 0.
        // Otherwise, if the two are equal, we return 0.
        // return (getAbsoluteTime(thisStart) - getAbsoluteTime(otherStart));
        return getAbsoluteTime(thisStart) - getAbsoluteTime(otherStart);
    }

    public static class Query extends ParseQuery<Event> {
        public Query() {
            super(Event.class);
        }

        /**
         * Modifies the query such that only the top 20 entries are returned.
         *
         * @return this: a modified query.
         */
        public Event.Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }

    public int getAbsoluteTime(String time)
    {
        String[] datePreArr = time.split(" |:");
        String year = datePreArr[7].substring(2, 4);
        String month = getValMonth(datePreArr[1]) + "";
        String[] dateArr = {month, datePreArr[2], year,
                datePreArr[3],
                datePreArr[4]};

        int[] dateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            dateInts[i] = Integer.parseInt(dateArr[i]);
        }

        int trueTime = (dateInts[0] * 43800) + (dateInts[1] * 1440)
                + (dateInts[2] * 525600) + (dateInts[3] * 60) + dateInts[4];

        return trueTime;
    }

    public int getValMonth(String prettyMonth)
    {
        switch(prettyMonth)
        {
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
        }
        return 0;
    }
}
