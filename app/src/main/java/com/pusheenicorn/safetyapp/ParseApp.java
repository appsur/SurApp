package com.pusheenicorn.safetyapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by jared1158 on 7/16/18.
 */

public class ParseApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        ParseUser.registerSubclass(ParseUser.class);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("sur-app")
                .clientKey("pusheenicorn")
                .server("http://sur-app.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);



        // New test creation of object below
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }
}
