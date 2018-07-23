package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class EventsActivity extends AppCompatActivity {
    //declared variables
    ImageButton ibBanner;
    BottomNavigationView bottomNavigationView;
    static final int REQUEST_CODE = 1;
    static final String TAG = "Success";
    private static int RESULT_LOAD_IMAGE = 1;

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
        ibBanner = (ImageButton) findViewById(R.id.ibBanner);
        ibBanner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //create a picture chooser from gallery
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if request code matches and the data is not null, set the image bitmap to be that of the picture
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            //get file path from the URI
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //set the banner to the image that is selected by the user
            ibBanner.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }
}
