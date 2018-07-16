package com.pusheenicorn.safetyapp;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class FriendsActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // get the Drawable custom_progressbar
        Drawable draw = getDrawable(R.drawable.customprogressbar);
        // set the drawable as progress drawable
//        progressBar.setProgressDrawable(draw);


    }
}
