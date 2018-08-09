package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {
    //declared variables for the buttons available on the page
    Button logInButton;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //initialized the parse user
        ParseUser currentUser = ParseUser.getCurrentUser();
        //makes sure that login or sign up occurs only if there is no current user
        if (currentUser != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        logInButton = findViewById(R.id.btnLogIn);
        signUpButton = findViewById(R.id.btnSignUp);
    }

    /**
     * take user to the login activity
     *
     * @param view notify which object is clicked
     */
    public void onLogInClick(View view) {
        //start intent to take user to the login activity class
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    /**
     * take user to the sign up activity
     *
     * @param view notify which object is clicked
     */
    public void onSignUpClick(View view) {
        //start intent to take user to the sign up activity class
        Intent signUp = new Intent(this, SignupActivity.class);
        startActivity(signUp);
    }
}
