package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Friend")
public class Friend extends ParseObject{
    private final static String KEY_USER = "user";
    private final static String KEY_TRACK_ME = "trackMe";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public boolean getTrackMe() {
        return getBoolean(KEY_TRACK_ME);
    }

    public void setTrackMe(boolean trackMe) {
        put(KEY_TRACK_ME, trackMe);
    }

    public static class Query extends ParseQuery<Friend> {
        public Query() {
            super(Friend.class);
        }

        public Friend.Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
