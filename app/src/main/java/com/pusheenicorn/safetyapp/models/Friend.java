package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Friend")
public class Friend extends ParseObject{
    private final static String KEY_USER = "user";
    private final static String KEY_NAME = "name";
    private final static String KEY_PERMISSIONS = "permissions";

    public User getUser()
    {
        User user;
        user = (User) getParseUser(KEY_USER);
        return user;
    }

    public void setUser(User user) {
        put(KEY_USER, user);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public List<Perm> getPermissions() {
        return getList(KEY_PERMISSIONS);
    }

    public void addPermission(Perm permission) {
        add(KEY_PERMISSIONS, permission);
    }

    public static class Query extends ParseQuery<Friend> {
        public Query() {
            super(Friend.class);
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
