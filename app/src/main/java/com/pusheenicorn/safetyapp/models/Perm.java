package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Perm")
public class Perm extends ParseObject{
    private final static String KEY_RING = "ringMe";
    private final static String KEY_TRACK = "trackMe";
    private final static String KEY_MESSAGE = "messageMe";
    private final static String KEY_CALL = "callMe";

    public boolean getRingPermissions() {
        return getBoolean(KEY_RING);
    }

    public void setRingPermissions(boolean permission) {
        put(KEY_RING, permission);
    }

    public boolean geTrackPermissions() {
        return getBoolean(KEY_TRACK);
    }

    public void setTrackPermissions(boolean permission) {
        put(KEY_TRACK, permission);
    }

    public boolean getMessagePermissions() {
        return getBoolean(KEY_MESSAGE);
    }

    public void setMessagePermissions(boolean permission) {
        put(KEY_MESSAGE, permission);
    }

    public boolean getCallPermissions() {
        return getBoolean(KEY_CALL);
    }

    public void setCallPermissions(boolean permission) {
        put(KEY_CALL, permission);
    }
}
