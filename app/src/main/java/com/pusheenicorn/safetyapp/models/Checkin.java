package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Checkin")
public class Checkin extends ParseObject {

    public static class Query extends ParseQuery<Checkin> {
        public Query() {
            super(Checkin.class);
        }

        public Query getTop() {
            setLimit(1);
            return this;
        }

        public Query findByUsername(String objectId) {
            whereEqualTo("objectId", objectId);
            return this;
        }
    }
}
