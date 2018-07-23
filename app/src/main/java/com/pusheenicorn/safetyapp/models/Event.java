package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Event")
public class Event extends ParseObject{
    private final static String KEY_NAME = "name";
    private final static String KEY_USERS = "usersAttending";
    private final static String KEY_START = "startTime";
    private final static String KEY_END = "endTime";

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name)
    {
        put(KEY_NAME, name);
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

}
