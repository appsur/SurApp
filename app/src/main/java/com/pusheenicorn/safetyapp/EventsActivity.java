package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsActivity extends AppCompatActivity {
    //declared variables
    ImageButton ibBanner;
    TextView tvEventTitle;
    BottomNavigationView bottomNavigationView;
    static final int REQUEST_CODE = 1;
    static final String TAG = "Success";
    private static int RESULT_LOAD_IMAGE = 1;
    Event currentEvent;
    Context context;

    EventUsersAdapter eventUsersAdapter;
    ArrayList<User> users;
    RecyclerView rvUsers;

    EventFriendsAdapter eventFriendsAdapter;
    ArrayList<Friend> friends;
    RecyclerView rvFriends;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        // Set the context
        context = this;
        // Get the current event and cast appropriately.
        currentEvent = (Event) getIntent().getParcelableExtra("event");
        Toast.makeText(EventsActivity.this, currentEvent.getObjectId(), Toast.LENGTH_LONG).show();
        tvEventTitle = findViewById(R.id.tvEventTitle);
        //added title to the event page
        tvEventTitle.setText(currentEvent.getName());
        //initialize bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        //create intent to take user to home page
                        Intent goHome = new Intent(EventsActivity.this,
                                MainActivity.class);
                        startActivity(goHome);
                        return true;
                    case R.id.action_message:
                        //create intent to take user to contacts page
                        Intent goMessage = new Intent(EventsActivity.this,
                                ContactActivity.class);
                        startActivity(goMessage);
                        return true;
                    case R.id.action_emergency:
                        //dial the number of a preset number
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                                "6304862146", null)));
                        return true;
                    case R.id.action_friends:
                        //create intent to take user to the friends page
                        Intent goFriends = new Intent(EventsActivity.this,
                                FriendsActivity.class);
                        startActivity(goFriends);
                        return true;
                }
                return true;
            }
        });

        //set banner to allow user to access gallery
        ibBanner = (ImageButton) findViewById(R.id.ibBanner);
        ParseFile bannerImage = currentEvent.getParseFile("bannerimage");

        if (bannerImage != null)
        {
            //load image using glide
            Glide.with(EventsActivity.this).load(bannerImage.getUrl())
                    .into(ibBanner);
        }

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

        // Get the current user
        currentUser = (User) ParseUser.getCurrentUser();

        // Find recycler views and hook up adapter
        rvUsers = (RecyclerView) findViewById(R.id.rvUsers);
        users = new ArrayList<User>();
        // construct the adapter from this data source
        eventUsersAdapter = new EventUsersAdapter(users);
        // recycler view setup
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(eventUsersAdapter);

        // Find recycler views and hook up adapter
        rvFriends = (RecyclerView) findViewById(R.id.rvFriends);
        friends = new ArrayList<Friend>();
        // construct the adapter from this data source
        eventFriendsAdapter = new EventFriendsAdapter(friends);
        // recycler view setup
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(eventFriendsAdapter);

        // Populate the recycler views appropriate
        loadEventUsers();
    }

    public void loadEventUsers() {
        final User.Query userQuery = new User.Query();
        userQuery.getTop();

        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    for (int i = objects.size() - 1; i > -1; i--) {
                        // If this user belongs to the event
                        if (currentEvent.getUsersIds().contains(objects.get(i).getObjectId())) {
                            // If this user is a friend, then...
                            if (currentUser.getFriendUsers().contains(objects.get(i).getObjectId()))
                            {
                                int index = currentUser.getFriendUsers()
                                        .indexOf(objects.get(i).getObjectId());
                                Friend newFriend = currentUser.getFriends().get(index);
                                friends.add(newFriend);
                                eventFriendsAdapter.notifyDataSetChanged();
                            }
                            else {
                                users.add(objects.get(i));
                                // notify the adapter
                                eventUsersAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    e.printStackTrace();
                }
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
            //convert bitmap to a parsefile
            final ParseFile parseFile = conversionBitmapParseFile(BitmapFactory.decodeFile(picturePath));
            //save in background so the image updates correctly
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    currentEvent.setBannerImage(parseFile);
                    currentEvent.put("bannerimage", parseFile);
                    currentEvent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                currentEvent.saveInBackground();
                                Log.d("EventsActivity", "Successfully updated!");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }

    //converts bitmap to parse file
    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png", imageByte);
        return parseFile;
    }

    public void onAddUser(View view) {
        final User.Query userQuery = new User.Query();
        userQuery.getTop().whereEqualTo("objectId", "5gDeswJDpG");

        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    if (objects != null)
                    {
                        currentEvent.addUser(objects.get(0));
                        eventFriendsAdapter.clear();
                        loadEventUsers();
                        currentEvent.saveInBackground();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

