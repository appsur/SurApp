package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {
    //declared variables
    BottomNavigationView bottomNavigationView;

    ProgressBar progressBar;

    FriendsAdapter friendAdapter;
    ArrayList<Friend> friends;
    RecyclerView rvFriendList;
    EditText etUsername;
    ImageButton ibAddFriend;
    ImageButton ibSearch;
    Context context;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        // Set the context.
        context = this;
        //setting the bottom navigation view
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        //create intent to take user to the home page
                        Intent returnHome = new Intent(FriendsActivity.this, MainActivity.class);
                        startActivity(returnHome);
                        return true;
                    case R.id.action_message:
                        //create intent to take user to the chat activity
                        Intent goMessages = new Intent(FriendsActivity.this, ContactActivity.class);
                        startActivity(goMessages);
                        return true;
                    case R.id.action_emergency:
                        //dial the number of a preset number
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                "6304862146", null)));
                        return true;
                    case R.id.action_friends:
                        //create toast to show user that they are already on the correct page
                        Toast.makeText(FriendsActivity.this, "You are already on the Friends page!", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_friends);

        rvFriendList = (RecyclerView) findViewById(R.id.rvFriendList);
        friends = new ArrayList<Friend>();
        // construct the adapter from this data source
        friendAdapter = new FriendsAdapter(friends);
        // recycler view setup
        rvFriendList.setLayoutManager(new LinearLayoutManager(this));
        rvFriendList.setAdapter(friendAdapter);
        populateFriendList();

        // declare username
        etUsername = (EditText) findViewById(R.id.etUsername);
        ibAddFriend = (ImageButton) findViewById(R.id.ibAddFriend);
        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
    }

    public void populateFriendList() {
        // Populate the friends list.
        final Friend.Query postQuery = new Friend.Query();
        postQuery.getTop().withUser();

        postQuery.findInBackground(new FindCallback<Friend>() {
            @Override
            public void done(List<Friend> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i > -1; i--)
                    {
                        // add the friend object
                        friends.add(objects.get(i));
                        // notify the adapter
                        friendAdapter.notifyDataSetChanged();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onSettings(View view) {
        //create intent to access the map activity and then toast the information
        Intent intent = new Intent(FriendsActivity.this, MapActivity.class);
        Toast.makeText(FriendsActivity.this, "Settings Page Accessed", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void onAddFriend(View view) {
        ibAddFriend.setVisibility(View.INVISIBLE);
        ibSearch.setVisibility(View.VISIBLE);
        etUsername.setVisibility(View.VISIBLE);
    }

    public void onSearch(View view) {
        String username = etUsername.getText().toString();

        // Get the actual checkin object by making a query.
        final User.Query userQuery = new User.Query();
        userQuery.getTop().whereEqualTo("username", username);
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, com.parse.ParseException e) {
                if (e == null) {
                    // Get the user that matches this name, if one exists.
                    if (objects.size() > 0)
                    {
                        user = objects.get(0);
                    }

                    // Otherwise, tell the user to try again.
                    else
                    {
                        Toast.makeText(context, "Sorry, that user doesn't exist",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    final Friend newFriend = new Friend();
                    newFriend.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            if (e == null) {
                                newFriend.setUser(user);
                                newFriend.setName(user.getName());
                                newFriend.saveInBackground();
                                friends.add(newFriend);
                                friendAdapter.notifyDataSetChanged();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    e.printStackTrace();
                }
            }
        });
    }
}
