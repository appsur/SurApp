package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    //declare variables
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;

    boolean ishaniIsTesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //logs the current user out if the parse user is not null to prevent errors
        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }
        //initialize variables
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogIn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                logIn(username, password);
            }
        });

    }

    public void logIn (String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {

            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity","Login Successful");
                    Intent intent;
                    if (ishaniIsTesting)
                    {
                        intent = new Intent(LoginActivity.this,
                                SettingsActivity.class);
                    }
                    else {
                        intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Log.d("LoginActivity", "Login failure.");
                    e.printStackTrace();
                }
            }
        });
    }

}
