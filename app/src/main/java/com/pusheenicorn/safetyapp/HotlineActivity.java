package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HotlineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotline);
    }

    public void onSettings(View view) {
        Intent i = new Intent(HotlineActivity.this, SettingsActivity.class);
        startActivity(i);
    }

    public void onSuicidePrevention(View view) {
        // Call: 1-800-827-7571
    }

    public void onDomesticAbuse(View view) {
        // Call: 1-800-799-SAFE
    }

}
