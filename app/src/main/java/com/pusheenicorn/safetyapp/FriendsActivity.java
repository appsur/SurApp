package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class FriendsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent returnHome = new Intent(FriendsActivity.this, MainActivity.class);
                    startActivity(returnHome);
                    return true;
                    case R.id.action_message:
                        Intent goMessages = new Intent (FriendsActivity.this, ContactActivity.class);
                        startActivity(goMessages);
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Toast.makeText(FriendsActivity.this, "You are already on the Friends page!", Toast.LENGTH_LONG ).show();
                        return true;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_friends);

    }

    public void onSettings(View view) {
        Intent intent = new Intent(FriendsActivity.this, MapActivity.class);
        Toast.makeText(FriendsActivity.this, "Settings Page Accessed", Toast.LENGTH_LONG ).show();
        startActivity(intent);
    }
}
