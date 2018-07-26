package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.pusheenicorn.safetyapp.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    // Define bottom navigation view.
    BottomNavigationView bottomNavigationView;

    // Define profile image view.
    ImageView ibProfileImage;
    public File photoFile;

    // Define variables for image toggle.
    ImageButton ibClock;
    ImageButton ibLocator;
    ImageButton ibCompass;
    ToggleButton tbSafe;
    ToggleButton tbLocation;
    ToggleButton tbRing;
    ToggleButton tbCheckin;
    EditText etNum;
    TextView tvNum;

    // ImageButton ibAlert;
    ImageButton ibEdit;
    Button btnDone;
    Button btnEditFrequency;
    boolean isClock = false;

    // Define variables for making frequency buttons appear.
    Button btnHourly;
    Button btnDaily;
    Button btnWeekly;

    private static int RESULT_LOAD_IMAGE = 1;

//    public File photoFile;
//    Uri photoURI;
//    static final int REQUEST_TAKE_PHOTO = 1;
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private static final String AUTHORITY = "com.pusheenicorn.sur-app";

    // Define global current user.
    User currentUser;

    // Define Text Views
    TextView tvNameValue;
    TextView tvPhoneValue;
    TextView tvUsernameValue;
    TextView tvCheckinFrequency;

    // Define Edit Texts
    EditText etUsername;
    EditText etPhoneNumber;
    EditText etName;

    //button for logging out
    private Button logOutButton;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        //log out button implementation
        logOutButton = findViewById(R.id.btnLogOut);
        logOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent logOut = new Intent(SettingsActivity.this, HomeActivity.class);
                ParseUser.logOut();
                startActivity(logOut);
            }
        });
        currentUser = (User) ParseUser.getCurrentUser();

        // Implementation of bottom navigation view.
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_person:
                        Intent personAction = new Intent(SettingsActivity.this,
                                MainActivity.class);
                        Toast.makeText(SettingsActivity.this, "Main Page Accessed",
                                Toast.LENGTH_LONG ).show();
                        startActivity(personAction);
                        return true;
                    case R.id.action_message:
                        // TODO -- link activities
                        return true;
                    case R.id.action_emergency:
                        // TODO -- link activities
                        return true;
                    case R.id.action_friends:
                        Intent friendsAction = new Intent(SettingsActivity.this,
                                FriendsActivity.class);
                        Toast.makeText(SettingsActivity.this, "Friends Page Accessed",
                                Toast.LENGTH_LONG ).show();
                        startActivity(friendsAction);
                        return true;
                }
                return true;
            }
        });

        // Initialize profile image view.
        ibProfileImage = (ImageButton) findViewById(R.id.ibProfileImage);
        ibProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create a picture chooser from gallery
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        // ibAlert = (ImageButton) findViewById(R.id.ibAlert);
        ibEdit = (ImageButton) findViewById(R.id.ibEdit);
        btnDone = (Button) findViewById(R.id.btnDone);

        // Initialize buttons for frequency setup.
        btnHourly = (Button) findViewById(R.id.btnHourly);
        btnWeekly = (Button) findViewById(R.id.btnWeekly);
        btnDaily = (Button) findViewById(R.id.btnDaily);

        // Initialize buttons for text view setup.
        tvUsernameValue = (TextView) findViewById(R.id.tvUsernameValue);
        tvPhoneValue = (TextView) findViewById(R.id.tvPhoneValue);
        tvNameValue = (TextView) findViewById(R.id.tvNameValue);
        tvCheckinFrequency = (TextView) findViewById(R.id.tvCheckinFrequency);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etName = (EditText) findViewById(R.id.etName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumberSettings);
        etNum = (EditText) findViewById(R.id.etNum);
        tvNum = (TextView) findViewById(R.id.tvNum);
        btnEditFrequency = (Button) findViewById(R.id.btnEditFrequency);

        // Get the current user and cast appropriately.

        // Set initial values for text views.
        tvUsernameValue.setText(currentUser.getUserName());
        tvNameValue.setText(currentUser.getName());
        tvCheckinFrequency.setText(currentUser.getFrequency());
        tvNum.setText(currentUser.getNotificationThreshold().toString());

        // Format the phone number and set the text view.
        String phoneNumber = currentUser.getPhonNumber();
        phoneNumber = "(" + phoneNumber.substring(0, 3) + ")"
                + phoneNumber.substring(3, 6) + "-"
                + phoneNumber.substring(6, 10);
        tvPhoneValue.setText(phoneNumber);

        // Load the profile image
        if (currentUser.getProfileImage() != null)
        {
            Glide.with(this).load(currentUser.getProfileImage()
                    .getUrl()).into(ibProfileImage);
        }

        setSafetyToggle();
        setLocationToggle();
        setRingToggle();
        setCheckinToggle();
    }

    // Set safety toggle
    public void setSafetyToggle()
    {
        // Set safety toggle
        tbSafe = (ToggleButton) findViewById(R.id.tbSafe);
        tbSafe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentUser.setSafe(true);
                    currentUser.saveInBackground();
                } else {
                    currentUser.setSafe(false);
                    currentUser.saveInBackground();
                }
            }
        });

        try {
            currentUser.fetch();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (currentUser.getSafe())
        {
            tbSafe.setChecked(true);
        }
        else
        {
            tbSafe.setChecked(false);
        }
    }

    // Set location toggle
    public void setLocationToggle()
    {
        // Set safety toggle
        tbLocation = (ToggleButton) findViewById(R.id.tbLocation);
        tbLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentUser.setTrackable(true);
                    currentUser.saveInBackground();
                } else {
                    currentUser.setTrackable(false);
                    currentUser.saveInBackground();
                }
            }
        });

        try {
            currentUser.fetch();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (currentUser.getTrackable())
        {
            tbLocation.setChecked(true);
        }
        else
        {
            tbLocation.setChecked(false);
        }
    }

    // Set ring toggle
    public void setRingToggle()
    {
        // Set safety toggle
        tbRing = (ToggleButton) findViewById(R.id.tbRing);
        tbRing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentUser.setRingable(true);
                    currentUser.saveInBackground();
                } else {
                    currentUser.setRingable(false);
                    currentUser.saveInBackground();
                }
            }
        });

        try {
            currentUser.fetch();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (currentUser.getRingable())
        {
            tbRing.setChecked(true);
        }
        else
        {
            tbRing.setChecked(false);
        }
    }

    // Set checkin toggle
    public void setCheckinToggle()
    {
        // Set safety toggle
        tbCheckin = (ToggleButton) findViewById(R.id.tbCheckin);
        tbCheckin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currentUser.setCheckme(true);
                    currentUser.saveInBackground();
                } else {
                    currentUser.setCheckme(false);
                    currentUser.saveInBackground();
                }
            }
        });

        try {
            currentUser.fetch();
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        if (currentUser.getCheckMe())
        {
            tbCheckin.setChecked(true);
        }
        else
        {
            tbCheckin.setChecked(false);
        }
    }

    /**
     * This function is called when the user clicks the clock button. It allows the user
     * to change the frequency of his/her settings
     *
     * @param view: the clock button
     */
    public void onClock(View view) {
        // UI response
        isClock = !isClock;
        if (isClock)
        {
            btnEditFrequency.setText("DONE");
            btnHourly.setVisibility(View.VISIBLE);
            btnDaily.setVisibility(View.VISIBLE);
            btnWeekly.setVisibility(View.VISIBLE);
            etNum.setText(tvNum.getText().toString());
            etNum.setVisibility(View.VISIBLE);
            tvNum.setVisibility(View.INVISIBLE);
        }
        else {
            // Functionality
            btnEditFrequency.setText("EDIT");
            btnHourly.setVisibility(View.INVISIBLE);
            btnDaily.setVisibility(View.INVISIBLE);
            btnWeekly.setVisibility(View.INVISIBLE);

            String newFrequency = etNum.getText().toString();
            tvNum.setText(newFrequency);

            etNum.setVisibility(View.INVISIBLE);
            tvNum.setVisibility(View.VISIBLE);
        }
    }

    /**
     * This function sets the user's checkin frequency to hourly when he/she clicks th
     * Hourly button.
     *
     * @param view: the hourly button view
     */
    public void onHourly(View view) {

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    final User user = (User) ParseUser.getCurrentUser();
                    user.setFrequency("Hourly");
                    user.saveInBackground();
                    tvCheckinFrequency.setText("Every hour");
                    isClock = !isClock;
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This function sets the user's checkin frequency to weekly when he/she clicks th
     * Weekly button.
     *
     * @param view: the weekly button view
     */
    public void onWeekly(View view) {
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    final User user = (User) ParseUser.getCurrentUser();
                    user.setFrequency("Weekly");
                    user.saveInBackground();
                    tvCheckinFrequency.setText(user.getFrequency());
                    isClock = !isClock;
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This function sets the user's checkin frequency to daily when he/she clicks th
     * Daily button.
     *
     * @param view: the daily button view
     */
    public void onDaily(View view) {
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    final User user = (User) ParseUser.getCurrentUser();
                    user.setFrequency("Daily");
                    user.saveInBackground();
                    tvCheckinFrequency.setText(user.getFrequency());
                    isClock = !isClock;
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Thsi function is called when the user clicks the edit icon. It makes the
     * edit views visible.
     * @param view: the edit button view.
     */
    public void onEdit(View view) {

        // Hide views
        ibEdit.setVisibility(View.INVISIBLE);
        tvUsernameValue.setVisibility(View.INVISIBLE);
        tvNameValue.setVisibility(View.INVISIBLE);
        tvPhoneValue.setVisibility(View.INVISIBLE);

        // Show edit views
        etUsername.setVisibility(View.VISIBLE);
        etPhoneNumber.setVisibility(View.VISIBLE);
        etName.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.VISIBLE);

        etUsername.setText(currentUser.getUserName());
        etName.setText(currentUser.getName());
        etPhoneNumber.setText(currentUser.getPhonNumber());
    }


    /**
     * This function is called when the user clicks the "done button." It updates the
     * user's data, updates the text views, and hides the edit views.
     * @param view: the done button view
     */
    public void onDone(View view) {

        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // save new data
                    final User user = (User) ParseUser.getCurrentUser();

                    String username = etUsername.getText().toString();
                    String phoneNumber = etPhoneNumber.getText().toString();
                    String name = etName.getText().toString();
                    String oldUsername = tvUsernameValue.getText().toString();
                    String oldPhoneNumber = tvPhoneValue.getText().toString();
                    String oldName = tvNameValue.getText().toString();

                    if (username != "") {
                        user.setUserName(username);
                    } else {
                        user.setUserName(oldUsername);
                    }

                    if (phoneNumber != "" && phoneNumber.length() >= 10) {
                        Toast.makeText(context, phoneNumber, Toast.LENGTH_LONG);
                        user.setPhoneNumber(phoneNumber);
                        String num = "(" + phoneNumber.substring(0, 3) + ") "
                                + phoneNumber.substring(3, 6) + "-"
                                + phoneNumber.substring(6, 10);
                        tvPhoneValue.setText(num);
                    } else {
                        Toast.makeText(context, phoneNumber, Toast.LENGTH_LONG);
                        user.setPhoneNumber(oldPhoneNumber);
                    }

                    if (name != "") {
                        user.setName(name);
                    } else {
                        user.setName(oldName);
                    }
                    user.saveInBackground();

                    // update text views
                    tvNameValue.setText(user.getName());
                    tvUsernameValue.setText(user.getUserName());
                    String newNumber = user.getPhonNumber();
                    if (newNumber.length() >= 20)
                    {
                        newNumber = "(" + phoneNumber.substring(0, 3) + ") "
                                + phoneNumber.substring(3, 6) + "-"
                                + phoneNumber.substring(6, 10);
                        tvPhoneValue.setText(newNumber);
                    }

                    // hide edit vies
                    etName.setVisibility(View.INVISIBLE);
                    etUsername.setVisibility(View.INVISIBLE);
                    etPhoneNumber.setVisibility(View.INVISIBLE);
                    btnDone.setVisibility(View.INVISIBLE);

                    // show text views
                    tvNameValue.setVisibility(View.VISIBLE);
                    tvUsernameValue.setVisibility(View.VISIBLE);
                    tvPhoneValue.setVisibility(View.VISIBLE);
                    ibEdit.setVisibility(View.VISIBLE);

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
            ibProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //convert bitmap to a parsefile
            final ParseFile parseFile =  conversionBitmapParseFile(BitmapFactory.decodeFile(picturePath));
            currentUser.setProfileImage(parseFile);
            //save in background so the image updates correctly
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    currentUser.put("profileimage", parseFile);
                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("SettingsActivity", "Successfully updated!");
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
    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }
}
