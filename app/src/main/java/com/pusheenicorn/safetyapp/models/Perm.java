package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Perm")
public class Perm extends ParseObject{
    private final static String KEY_NAME = "name";
    private final static String KEY_VAL = "allowed";

    public final static String TRACKING_NAME = "TrackMe";
    public final static String RINGING_NAME = "RingMe";

    public String getPermName() {
        return getString(KEY_NAME);
    }

    public void setPermName(String name) {
        put(KEY_NAME, name);
    }

    public boolean getPermVal() {
        return getBoolean(KEY_VAL);
    }

    public void setPermVal(boolean val) {
        put(KEY_VAL, val);
    }
}
