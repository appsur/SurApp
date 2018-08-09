package com.pusheenicorn.safetyapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    //declare variables
    EditText etUsername;
    EditText etPassword;
    EditText etEmail;
    EditText etPhoneNumber;
    Button btnSignUp;
    User user;
    ImageButton ibProfileImage;
    public File photoFile;
    Uri photoURI;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String AUTHORITY = "com.pusheenicorn.safetyapp";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        checkPermissionsPlease();
        //log out the current user if they aren't null
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }
        // Create the ParseUser
        user = (User) ParseUser.create("_User");
        //initialize variables
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmail = (EditText) findViewById(R.id.etEmail);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        etPhoneNumber = (EditText) findViewById(R.id.tvPhoneNumberSettings);
        ibProfileImage = (ImageButton) findViewById(R.id.ibProfileImage);
        ibProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent(v);
            }
        });
    }

    /**
     * check permissions for user to upload an image
     */
    private void checkPermissionsPlease() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }
    }

    /**
     * takes user information and creates a new user (sets defaults for parameters that are not set in sign up)
     *
     * @param v current view
     */
    public void signUpOnClick(View v) {
        //declare and initialize variable sfor sign up
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        //create new parsefile with the profile image of the user
        final ParseFile parseFile = new ParseFile(new File(photoFile.getAbsolutePath()));

        // Set core properties
        user.put("username", username);
        user.put("password", password);
        user.put("name", "NAME");
        user.put("email", email);
        user.put("phonenumber", phoneNumber);
        user.put("trackable", false);
        user.put("ringable", false);
        user.put("primaryContact", "WIfsetxLFu");
        int three = 3;
        Number num = (Number) three;
        user.put("notify", num);

        //prevent crashing by setting user check in to a default value
        final Checkin checkin = new Checkin();
        checkin.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    checkin.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });

        final User.Query userQuery = new User.Query();
        userQuery.whereEqualTo("objectId", "WIfsetxLFu");
        userQuery.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> objects, ParseException e) {
                if (e == null) {
                    final User newUser = objects.get(0);
                    user.put("primaryContact", newUser);
                } else {
                    e.printStackTrace();
                }
            }
        });

        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.put("profileimage", parseFile);
                user.setLastCheckin(checkin);
                user.setFrequency("Hourly");
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("SignupActivity", "Sign Up Successful");
                            Intent intent = new Intent(SignupActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("SignupActivity", "Sign up failure.");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if the correct request code is used, set the profile image to the compressed bitmap for the image taken
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);


            ibProfileImage.setImageBitmap(bitmap);

        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //create the file where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                //error occurred while creating the File
            }
            //continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        AUTHORITY,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
