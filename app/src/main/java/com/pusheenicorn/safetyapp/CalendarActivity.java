package com.pusheenicorn.safetyapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

/**
 * Created by ganesh on 6/10/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    private TextView thedate;
    private Button btngocalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        thedate = (TextView) findViewById(R.id.date);
        btngocalendar = (Button) findViewById(R.id.btngocalendar);

        Intent incoming = getIntent();
        boolean isStarting = incoming.getBooleanExtra("starting", false);
        if (!isStarting)
        {
            String date = incoming.getStringExtra("date");
            thedate.setText(date);
        }

        btngocalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarActivity.this, CalendarChoiceActivity.class);
                startActivity(intent);
            }
        });
    }
}
