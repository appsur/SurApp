package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton ibEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        ibEvents = (ImageButton) findViewById(R.id.ibEvents);
        ibEvents.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent goEvents = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(goEvents);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        // TODO -- link activities
                        return true;
                    case R.id.action_message:
                        // TODO -- link activities
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(MainActivity.this, FriendsActivity.class);
                        Toast.makeText(MainActivity.this, "Friends Page Accessed", Toast.LENGTH_LONG ).show();
                        startActivity(friendsAction);
                        return true;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_person);
    }

    public void onSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        Toast.makeText(MainActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG ).show();
        startActivity(intent);
    }
}
