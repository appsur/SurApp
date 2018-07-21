package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FriendsActivity extends AppCompatActivity {
    //declared variables
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
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

    }

    public void onSettings(View view) {
        //create intent to access the map activity and then toast the information
        Intent intent = new Intent(FriendsActivity.this, MapActivity.class);
        Toast.makeText(FriendsActivity.this, "Settings Page Accessed", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}
