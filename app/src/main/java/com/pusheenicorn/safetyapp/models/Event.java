package com.pusheenicorn.safetyapp.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pusheenicorn.safetyapp.utils.CalendarUtil;
import com.pusheenicorn.safetyapp.utils.NotificationUtil;

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
    private static final CalendarUtil calendarUtil = new CalendarUtil();

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public String getName() {
        try {
            return fetchIfNeeded().getString(KEY_NAME);
        } catch (ParseException e) {
            e.printStackTrace();
            return "saddnesss";
        }
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
        return calendarUtil.getAbsoluteTime(thisStart) - calendarUtil.getAbsoluteTime(otherStart);
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
    }

    public List<String> getUserNames() {
        ArrayList<String> userNames = new ArrayList<String>();
        if (getUsers() != null) {
            for (int i = 0; i < getUsers().size(); i++)
            {
                try {
                    userNames.add(getUsers().get(i).fetchIfNeeded().getString("username"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return userNames;
    }

    public void sendNotifications(String id, NotificationUtil notificationUtil, Context context, int notifId) {
        ArrayList<Alert> alerts = (ArrayList) getAlerts();
        if (alerts == null || alerts.isEmpty())
        {
            return;
        }
        else {
            for (int i = 0; i < alerts.size(); i++)
            {
                Alert alert = alerts.get(i);
                if (alert.getSeenBy() == null || alert.getSeenBy().isEmpty() ||
                        alert.getSeenBy().contains(id))
                {
                    String message = getName() + " alert: " + alert.getMessage()
                            + "\n (from " +
                            alert.getUsername() + " )";

                    notificationUtil
                            .scheduleNotification(notificationUtil
                                    .getAlertNotification(message), notifId, 0);

                    alert.addSeenBy(id);
                    alert.saveInBackground();
                }
            }
        }
    }
}
