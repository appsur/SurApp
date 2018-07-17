package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

@ParseClassName("User")
public class User extends ParseObject {
    private final static String KEY_USER = "username";
    private final static String CREATED_AT = "createdAt";
    private final static String KEY_EMAIL = "email";
    private final static String PROF_IMAGE = "profileimage";
    private final static String NAME = "name";
    private final static String PHONE_NUMBER = "phonenumber";
    private final static String TRACKABLE = "trackable";
    private final static String SAFE = "safe";
    private final static String LOCATION = "location";

    public String getUserName() {
        return getString(KEY_USER);
    }

    public void setUserName(String username) {
        put(KEY_USER, username);
    }

    public String getCreated() {
        return getString(CREATED_AT);
    }

    public void setCreated() {
        
    }



    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }


}
