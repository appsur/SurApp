package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("_User")
public class ParseUser extends ParseObject {
    private final static String KEY_USERNAME = "username";
    private final static String KEY_PROFILE_IMAGE = "profileimage";
    private final static String KEY_NAME = "name";
    private final static String KEY_PHONE_NUMBER = "phonenumber";
    private final static String KEY_TRACKABLE = "trackable";
    private final static String KEY_SAFE = "safe";
    private final static String KEY_LOCATION = "location";

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

    public static class Query extends ParseQuery<ParseUser> {
        public Query() {
            super(ParseUser.class);
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
