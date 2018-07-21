package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class EventsActivity extends AppCompatActivity {
    //declared variables
    ImageButton ibBanner;
    BottomNavigationView bottomNavigationView;
    static final int REQUEST_CODE = 1;
    static final String TAG = "Success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        //initialize bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        //create intent to take user to home page
                        Intent goHome = new Intent(EventsActivity.this, MainActivity.class);
                        Toast.makeText(EventsActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        startActivity(goHome);
                        return true;
                    case R.id.action_message:
                        //create intent to take user to contacts page
                        Intent goMessage = new Intent(EventsActivity.this, ContactActivity.class);
                        startActivity(goMessage);
                        return true;
                    case R.id.action_emergency:
                        //dial the number of a preset number
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                "6304862146", null)));
                        return true;
                    case R.id.action_friends:
                        //create intent to take user to the friends page
                        Toast.makeText(EventsActivity.this, "Already on Friends Page!", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return true;
            }
        });

        //set banner to allow user to access gallery
        //TODO - allow user to upload image
        ibBanner = (ImageButton) findViewById(R.id.ibBanner);
        ibBanner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        //data gives you the image uri. Try to convert that to bitmap
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(TAG, "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }
}
