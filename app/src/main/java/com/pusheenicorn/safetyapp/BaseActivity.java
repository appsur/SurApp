package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {
//    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        public void setNavigationDestinations(final Activity activity, final Context mContext) {
//        }
    }
    public void setNavigationDestinations (final Activity activity, BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_person:
//                                Intent i = new Intent();
//                                i.setClassName("com.pusheenicorn.safetyapp",
//                                        "com.pusheenicorn.safetyapp." +
//                                                "MainActivity");
//                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(i);
                                Intent goHome = new Intent(activity, MainActivity.class);
                                startActivity(goHome);
                                return true;
                            case R.id.action_message:
//                                Intent j = new Intent();
//                                j.setClassName("com.pusheenicorn.safetyapp",
//                                        "com.pusheenicorn.safetyapp." +
//                                                "ContactActivity");
//                                j.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(j);
                                Intent contactAction = new Intent(activity,
                                        ContactActivity.class);
                                startActivity(contactAction);
                                return true;
                            case R.id.action_emergency:
//                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
//                                        "6304862146", null)));

                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                        "6304862146", null)));
                                return true;
                            case R.id.action_friends:
//                                Intent l = new Intent();
//                                l.setClassName("com.pusheenicorn.safetyapp",
//                                        "com.pusheenicorn.safetyapp." +
//                                                "FriendsActivity");
//                                l.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(l);
                                Intent friendsAction = new Intent(activity,
                                        FriendsActivity.class);
                                startActivity(friendsAction);
                                return true;
                        }
                        return true;
                    }
                });
    }

}
