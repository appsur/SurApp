package com.pusheenicorn.safetyapp;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.pusheenicorn.safetyapp.models.User;

import java.util.ArrayList;

public class FriendsActivity extends AppCompatActivity {
    ProgressBar progressBar;
    FriendsAdapter friendsAdapter;
    ArrayList<User> friends;
    RecyclerView rvFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        rvFriends = (RecyclerView) findViewById(R.id.rvFriendList);
        friends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(friends);

        //setting up the Recycler View
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        //setting up the adapter
        rvFriends.setAdapter(friendsAdapter);



    }
}
