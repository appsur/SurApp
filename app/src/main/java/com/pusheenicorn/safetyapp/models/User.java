package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {
    private final static String KEY_USERNAME = "username";
    private final static String KEY_PROFILE_IMAGE = "profileimage";
    private final static String KEY_NAME = "name";
    private final static String KEY_PHONE_NUMBER = "phonenumber";
    private final static String KEY_TRACKABLE = "trackable";
    private final static String KEY_SAFE = "safe";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_RINGABLE = "ringable";
    private final static String KEY_FREQUENCY = "checkin";

    public String getUserName() {
        return getString(KEY_USERNAME);
    }

    public void setUserName(String username) {
        put(KEY_USERNAME, username);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile parseFile) {
        put(KEY_PROFILE_IMAGE, parseFile);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getPhonNumber() {
        return getString(KEY_PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        put(KEY_PHONE_NUMBER, phoneNumber);
    }

    public boolean getTrackable() {
        return getBoolean(KEY_TRACKABLE);
    }

    public void setTrackable(boolean trackable) {
        put(KEY_TRACKABLE, trackable);
    }

    public boolean getSafe() {
        return getBoolean(KEY_SAFE);
    }

    public void setSafe(boolean safe) {
        put(KEY_SAFE, safe);
    }

    public String getLocation() {
        return getString(KEY_LOCATION);
    }

    public void setLocation(String location) {
        put(KEY_LOCATION, location);
    }

    public boolean getRingable() {
        return getBoolean(KEY_RINGABLE);
    }

    public void setRingable(boolean ringable) {
        put(KEY_RINGABLE, ringable);
    }

    public String getFrequency() {
        int num = (int) getNumber(KEY_FREQUENCY);
        switch (num) {
            case 10080:
                return "Every week";
            case 1440:
                return "Every day";
            case 60:
                return "Every hour";
            default:
                return "Error";
        }
    }

    public void setFrequency(String frequency) {
        if (frequency.equals("Daily")) {
            put(KEY_FREQUENCY, 1440);
        }

        else if (frequency.equals("Hourly")) {
            put(KEY_FREQUENCY, 60);
        }

        else if (frequency.equals("Weekly")) {
            put(KEY_FREQUENCY, 10080);
        }
    }

    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

//        public Query withUser() {
////            include("user");
////            return this;
////        }
    }


}
