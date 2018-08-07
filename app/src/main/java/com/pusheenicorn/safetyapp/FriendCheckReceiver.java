package com.pusheenicorn.safetyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.FriendAlert;
import com.pusheenicorn.safetyapp.models.User;

/**
 * Created by jared1158 on 7/31/18.
 */

public class FriendCheckReceiver extends BroadcastReceiver {
    Context mContext;
    FriendAlert alert;
    User currentUser;
    public int count = 0;
    public int MAX = 10;


    @Override
    public void onReceive(Context context , Intent intent){
        //if (count < MAX) {

        Toast.makeText(context, "hello", Toast.LENGTH_LONG).show();
        mContext = context;
        if (currentUser != null) {
            friendsCheck();
        }
            //count += 1;
        //}
    }



    public void friendsCheck(){
        alert = new FriendAlert();
        currentUser = (User) ParseUser.getCurrentUser();
        alert.alertNeeded(mContext);
    }

}
