package com.pusheenicorn.safetyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarChoiceActivity extends AppCompatActivity {

    private static final String TAG = "CalendarChoiceActivity";
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        final boolean start = getIntent().getBooleanExtra(MainActivity.START_KEY, false);
        final boolean end = getIntent().getBooleanExtra(MainActivity.END_KEY, false);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView CalendarView, int year,
                                            int month, int dayOfMonth) {
                // Month starts counting from 0, so add 1 to get traditional format
                month++;
                // Get the day of the week
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                TimeZone tz = calendar.getTimeZone();
                String weekday = "";

                switch(dayOfWeek)
                {
                    case (1):
                        weekday = "SUN";
                        break;
                    case (2):
                        weekday = "MON";
                        break;
                    case (3):
                        weekday = "TUE";
                        break;
                    case (4):
                        weekday = "WED";
                        break;
                    case (5):
                        weekday = "THU";
                        break;
                    case (6):
                        weekday = "FRI";
                        break;
                    case (7):
                        weekday = "SAT";
                        break;
                }

                String date = String.format("%s %s/%s/%s %s", weekday, month, dayOfMonth, year,
                        getNiceZone(tz.getDisplayName()));
                Log.d(TAG, "onSelectedDayChange: yyyy/mm/dd:" + date);
                Intent intent = new Intent(CalendarChoiceActivity.this,
                        MainActivity.class);
                if (start)
                {
                    intent.putExtra(MainActivity.START_KEY, true);
                }
                else if (end)
                {
                    intent.putExtra(MainActivity.END_KEY, true);
                }
                intent.putExtra(MainActivity.DATE_KEY, date);
                intent.putExtra(MainActivity.FROM_CALENDAR, true);
                startActivity(intent);

            }
        });
    }

    public String getNiceZone(String zone)
    {
        String[] zoneArr = zone.split(" ");
        String niceZone = "";
        for (int i = 0; i < zoneArr.length; i++)
        {
            niceZone += zoneArr[i].charAt(0);
        }
        return niceZone;
    }
}