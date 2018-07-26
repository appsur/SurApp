package com.pusheenicorn.safetyapp.models;

import android.content.Context;

import com.parse.FindCallback;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jared1158 on 7/25/18.
 */

public class FriendAlert {
    Context context;
    List<Friend> friends;
    User currentUser;
    User myFriend;





    public int timeSinceLastCheckin (Date prevDate, User user) {
        // Define format type.
        DateFormat df = new SimpleDateFormat("MM/dd/yy/HH/mm");

        // Get current Date.
        Date currDate = new Date();

        // Split by regex "/" convert to int array and find time difference.
        String[] currDateArr = df.format(currDate).split("/");
        String[] prevDateArr = df.format(prevDate).split("/");
        int[] currDateInts = new int[5];
        int[] prevDateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            currDateInts[i] = Integer.parseInt(currDateArr[i]);
            prevDateInts[i] = Integer.parseInt(prevDateArr[i]);
        }

        // true curr - truePrev is the number of minutes ago that the user checked in
        int trueCurr = (currDateInts[0] * 43800) + (currDateInts[1] * 1440)
                + (currDateInts[2] * 525600) + (currDateInts[3] * 60) + currDateInts[4];
        int truePrev = (prevDateInts[0] * 43800) + (prevDateInts[1] * 1440)
                + (prevDateInts[2] * 525600) + (prevDateInts[3] * 60) + prevDateInts[4];
        // threshold is the length of a user's checkin cycle
        //int threshold = (int) user.getNumber("checkin");

        return (trueCurr - truePrev);
    }

    public void alertNeeded(User user, int time) {
        currentUser = (User) ParseUser.getCurrentUser();
        friends = currentUser.getFriends();


        
        //iterate through friends and check each friend's check in status
        for (Friend fr : friends) {
            myFriend = (User) fr.getUser();
            if (myFriend.getPrimary() == currentUser){

                //assign friend's last checkin to a date object
                Date date = myFriend.getLastCheckin().getCreatedAt();
                int placeholder = 5;
                int cycle = (int) myFriend.getNumber("checkin");
                if (timeSinceLastCheckin(date, myFriend ) > (placeholder * cycle) ){
                    //TODO: send notification
                }
            }
        }
        


    }
    




}
