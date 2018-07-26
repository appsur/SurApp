package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {
    //declared variables for the buttons available on the page
    Button logInButton;
    Button signUpButton;
    boolean isIshaniTesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //initialized the parse user
        ParseUser currentUser = ParseUser.getCurrentUser();
        //makes sure that login or sign up occurs only if there is no current user
        if (currentUser != null && isIshaniTesting) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        else if (currentUser != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
    }

    public void onLogInClick(View view) {
        //start intent to take user to the login activity class
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void onSignUpClick(View view) {
        //start intent to take user to the sign up activity class
        Intent signUp = new Intent(this, SignupActivity.class);
        startActivity(signUp);
    }
}
