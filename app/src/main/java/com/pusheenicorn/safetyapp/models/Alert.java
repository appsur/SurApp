package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

@ParseClassName("Alert")
public class Alert extends ParseObject {
    private static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_SEEN = "seenBy";

    public String getMessage() {
        try {
            return fetchIfNeeded().getString("message");
        } catch (ParseException e) {
            return "error retrieving the message";
        }
    }
    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    public String getName() {
        return getString(KEY_NAME);
    }
    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getUsername() {
        return getString(KEY_USERNAME);
    }
    public void setUserame(String username) {
        put(KEY_USERNAME, username);
    }

    public List<String> getSeenBy() {
        return getList(KEY_SEEN);
    }

    public void addSeenBy(String objectId) {
        add(KEY_SEEN, objectId);
    }

    public static class Query extends ParseQuery<Alert> {
        public Query() {
            super(Alert.class);
        }

        /**
         * Modifies the query such that only the top 20 entries are returned.
         * @return this: a modified query.
         */
        public Alert.Query getTop() {
            setLimit(20);
            return this;
        }
    }
}
