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
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.Set;

public class SettingsActivity extends BaseActivity {

    // Define bottom navigation view.
    BottomNavigationView bottomNavigationView;

    //variables for the draw out menu
    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<MainActivity.NavItem> mNavItems = new ArrayList<MainActivity.NavItem>();

    // Define profile image view.
    private ImageView ibProfileImage;
    public File photoFile;

    // Define variables for image toggle.
    private ToggleButton tbSafe;
    private ToggleButton tbLocation;
    private ToggleButton tbRing;
    private ToggleButton tbCheckin;
    private EditText etNum;
    private TextView tvNum;

    // ImageButton ibAlert;
    private ImageButton ibEdit;
    private Button btnDone;
    private Button btnEditFrequency;
    boolean isClock = false;

    // Define variables for making frequency buttons appear.
    Button btnHourly;
    Button btnDaily;
    Button btnWeekly;

    private static int RESULT_LOAD_IMAGE = 1;
    public static final String PROFILE_KEY = "profileimage";

//    public File photoFile;
//    Uri photoURI;
//    static final int REQUEST_TAKE_PHOTO = 1;
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private static final String AUTHORITY = "com.pusheenicorn.sur-app";

    // Define global current user.
    private User currentUser;

    // Define Text Views
    private TextView tvNameValue;
    private TextView tvPhoneValue;
    private TextView tvUsernameValue;
    private TextView tvCheckinFrequency;

    // Define Edit Texts
    private EditText etUsername;
    private EditText etPhoneNumber;
    private EditText etName;

    //button for logging out
    private Button logOutButton;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
        implementLogout();
        //set and populated the bottom navigation view
        bottomNavigationView = findViewById(R.id.bottom_navigation_settings);
        setNavigationDestinations(SettingsActivity.this, bottomNavigationView);

        initializeNavItems(mNavItems);
        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);
        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position, mDrawerList, mDrawerPane, mDrawerLayout,
                        SettingsActivity.this, mNavItems);
            }
        });

        setViews();
        setSafetyToggle();
        setLocationToggle();
        setRingToggle();
        setCheckinToggle();
    }

    public void implementLogout() {
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

    }

    public void setViews() {

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
        if (currentUser.getProfileImage() != null) {
            Glide.with(this).load(currentUser.getProfileImage()
                    .getUrl()).into(ibProfileImage);
        }
    }

    // Set safety toggle
    public void setSafetyToggle() {
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentUser.getSafe()) {
            tbSafe.setChecked(true);
        } else {
            tbSafe.setChecked(false);
        }
    }

    // Set location toggle
    public void setLocationToggle() {
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentUser.getTrackable()) {
            tbLocation.setChecked(true);
        } else {
            tbLocation.setChecked(false);
        }
    }

    // Set ring toggle
    public void setRingToggle() {
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentUser.getRingable()) {
            tbRing.setChecked(true);
        } else {
            tbRing.setChecked(false);
        }
    }

    // Set checkin toggle
    public void setCheckinToggle() {
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentUser.getCheckMe()) {
            tbCheckin.setChecked(true);
        } else {
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
        if (isClock) {
            btnEditFrequency.setText("DONE");
            btnHourly.setVisibility(View.VISIBLE);
            btnDaily.setVisibility(View.VISIBLE);
            btnWeekly.setVisibility(View.VISIBLE);
            etNum.setText(tvNum.getText().toString());
            etNum.setVisibility(View.VISIBLE);
            tvNum.setVisibility(View.INVISIBLE);
        } else {
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
                if (e == null) {
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
                if (e == null) {
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
                if (e == null) {
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
     *
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
     *
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
                        user.setPhoneNumber(phoneNumber);
                        String num = "(" + phoneNumber.substring(0, 3) + ") "
                                + phoneNumber.substring(3, 6) + "-"
                                + phoneNumber.substring(6, 10);
                        tvPhoneValue.setText(num);
                    } else {
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
                    if (newNumber.length() >= 20) {
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
            final ParseFile parseFile = conversionBitmapParseFile(BitmapFactory.decodeFile(picturePath));
            currentUser.setProfileImage(parseFile);
            //save in background so the image updates correctly
            parseFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    currentUser.put(PROFILE_KEY, parseFile);
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
    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png", imageByte);
        return parseFile;
    }
}
