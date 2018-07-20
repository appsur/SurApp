package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * The Checkin class extends ParseObject. It represents a
 * user checkin. In this class, there is a public Query
 * class to make queries for checkin objects in the Parse
 * database.
 */
@ParseClassName("Checkin")
public class Checkin extends ParseObject {
    public static class Query extends ParseQuery<Checkin> {
        public Query() {
            super(Checkin.class);
        }

        /**
         * Modifies the query such that only the topmost entry is returned.
         * @return this: a modified query.
         */
        public Query getTop() {
            setLimit(1);
            return this;
        }
    }
}
