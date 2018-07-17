package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Calendar;
import java.util.Date;

@ParseClassName("User")
public class User {
    private final static String KEY_USER = "username";
    private final static String CREATED_AT = "createdAt";

//
//    public String getUser() {
//        return getString(KEY_USER);
//    }
//
//    public void setUser(String description) {
//        put(KEY_USER, user);
//    }
//
//
//    public static class Query extends ParseQuery<User> {
//        public Query() {
//            super(User.class);
//        }
//
//        public Query getTop() {
//            setLimit(20);
//            return this;
//        }
//
//        public Query withUser() {
//            include("user");
//            return this;
//        }
//    }


}
