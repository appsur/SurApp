package com.pusheenicorn.safetyapp.utils;

import android.text.format.DateUtils;

import com.pusheenicorn.safetyapp.MainActivity;
import com.pusheenicorn.safetyapp.models.Checkin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarUtil {

    public static final int YEAR_CONVERSION = 525600;
    public static final int MONTH_CONVERSION = 43800;
    public static final int DAY_CONVERSION = 1440;
    public static final int HOUR_CONVERSION = 60;

    public int getAbsoluteTime(String time)
    {
        String[] datePreArr = time.split(" |:");
        String year = datePreArr[7].substring(2, 4);
        String month = getValMonth(datePreArr[1]) + "";
        String[] dateArr = {month, datePreArr[2], year,
                datePreArr[3],
                datePreArr[4]};

        int[] dateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            dateInts[i] = Integer.parseInt(dateArr[i]);
        }

        int trueTime = (dateInts[0] * MONTH_CONVERSION) + (dateInts[1] * DAY_CONVERSION)
                + (dateInts[2] * YEAR_CONVERSION) + (dateInts[3] * HOUR_CONVERSION) + dateInts[4];

        return trueTime;
    }

    public int getValMonth(String prettyMonth)
    {
        switch(prettyMonth)
        {
            case "JAN":
                return 1;
            case "FEB":
                return 2;
            case "MAR":
                return 3;
            case "APR":
                return 4;
            case "MAY":
                return 5;
            case "JUN":
                return 6;
            case "JUL":
                return 7;
            case "AUG":
                return 8;
            case "SEP":
                return 9;
            case "OCT":
                return 10;
            case "NOV":
                return 11;
            case "DEC":
                return 12;
        }
        return 0;
    }

    public String getPrettyMonth(String month) {
        String prettyMonth = month;
        switch(month)
        {
            case "1":
                month = "JAN";
                break;
            case "2":
                month = "FEB";
                break;
            case "3":
                month = "MAR";
                break;
            case "4":
                month = "APR";
                break;
            case "5":
                month = "MAY";
                break;
            case "6":
                month = "JUN";
                break;
            case "7":
                month = "JUL";
                break;
            case "8":
                month = "AUG";
                break;
            case "9":
                month = "SEP";
                break;
            case "10":
                month = "OCT";
                break;
            case "11":
                month = "NOV";
                break;
            case "12":
                month = "DEC";
                break;
        }
        return month;
    }

    public String getPrettyYear(String year)
    {
        String prettyYear = year;
        if (year.length() < 4)
        {
            prettyYear = "20" + year;
        }
        return prettyYear;
    }

    public String getPrettyDay(String day)
    {
        String prettyDay = day;
        if (day.length() < 2)
        {
            prettyDay = "0" + day;
        }
        return prettyDay;
    }

    /**
     * This function returns the relative time between the current time and a passed in date
     *
     * @param rawDate: a String date formatted as "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
     * @return relativeDate: the relative time between rawDate and present
     */
    public String getRelativeTimeAgo(String rawDate) {
        SimpleDateFormat sf = new SimpleDateFormat(MainActivity.TWITTER_FORMAT, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}
