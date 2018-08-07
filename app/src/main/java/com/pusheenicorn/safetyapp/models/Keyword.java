package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Keyword")
public class Keyword extends ParseObject {
    private final static String KEYWORD = "keyword";

    public String getKeyword() {
        return getString(KEYWORD);
    }
    public void setKeyword(String keyword) {
        put(KEYWORD, keyword);
    }
    public static class Query extends ParseQuery<Keyword> {
        public Query() {
            super(Keyword.class);
        }

        /**
         * Modifies the query such that only the top 20 entries are returned.
         *
         * @return this: a modified query.
         */
        public Keyword.Query getTop() {
            setLimit(20);
            return this;
        }
    }

}
