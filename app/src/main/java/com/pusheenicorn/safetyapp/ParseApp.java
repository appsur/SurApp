package com.pusheenicorn.safetyapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.Keyword;
import com.pusheenicorn.safetyapp.models.Perm;
import com.pusheenicorn.safetyapp.models.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by jared1158 on 7/16/18.
 */

public class ParseApp extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Friend.class);
        ParseObject.registerSubclass(Checkin.class);
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Perm.class);
        ParseObject.registerSubclass(Alert.class);
        ParseObject.registerSubclass(Keyword.class);

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
    }
}
