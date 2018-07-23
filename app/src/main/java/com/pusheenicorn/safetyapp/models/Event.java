package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Event")
public class Event extends ParseObject{
    private final static String KEY_NAME = "name";
    private final static String KEY_USERS = "usersAttending";
    private final static String KEY_START = "startTime";
    private final static String KEY_END = "endTime";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_BANNER_IMAGE = "bannerimage";

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name)
    {
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

    public void setStart(String start)
    {
        put(KEY_START, start);
    }

    public String getEnd() {
        return getString(KEY_END);
    }

    public void setEnd(String end)
    {
        put(KEY_END, end);
    }

    public List<User> getUsers() {
        return getList(KEY_USERS);
    }

    public void addUser(User user) {
        add(KEY_USERS, user);
    }

    public static class Query extends ParseQuery<Event> {
        public Query() {
            super(Event.class);
        }

        /**
         * Modifies the query such that only the top 20 entries are returned.
         * @return this: a modified query.
         */
        public Event.Query getTop() {
            setLimit(20);
            return this;
        }
    }
}
