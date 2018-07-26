package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.parse.ParseUser;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
    public void setNavigationDestinations (final Activity activity, BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_person:
                                Intent goHome = new Intent(activity, MainActivity.class);
                                startActivity(goHome);
                                return true;
                            case R.id.action_message:
                                Intent contactAction = new Intent(activity,
                                        ContactActivity.class);
                                startActivity(contactAction);
                                return true;
                            case R.id.action_emergency:
                                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                        "6304862146", null)));
                                return true;
                            case R.id.action_friends:
                                Intent friendsAction = new Intent(activity,
                                        FriendsActivity.class);
                                startActivity(friendsAction);
                                return true;
                        }
                        return true;
                    }
                });
    }

    public void selectItemFromDrawer(int position, ListView mDrawerList, RelativeLayout mDrawerPane,
                                     DrawerLayout mDrawerLayout, Activity activity) {
        // Locate Position
        switch (position) {
            case 0:
                startActivity(new Intent(activity, MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(activity, ContactActivity.class));
                break;
            case 2:
                startActivity(new Intent(activity, ChatActivity.class));
                break;
            case 3:
                startActivity(new Intent(activity, FriendsActivity.class));
                break;
            case 4:
                Intent logOut = new Intent(activity, HomeActivity.class);
                ParseUser.logOut();
                startActivity(logOut);
        }

        mDrawerList.setItemChecked(position, true);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

}
