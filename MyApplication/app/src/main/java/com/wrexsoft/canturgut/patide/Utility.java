package com.wrexsoft.canturgut.patide;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Utility extends Fragment {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    static DatabaseReference dref = FirebaseDatabase.getInstance().getReference();

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public static ArrayList<String> readCalendarEvent(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String userID = settings.getString("FbUserId", "userID");
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < CNames.length; i++) {
            Date date = new Date();
            Log.d("trytoreaddatabase", cursor.getString(1) + " -- " + (date.getTime() - Long.parseLong(cursor.getString(3))));
            if ((date.getTime() - Long.parseLong(cursor.getString(3))) < 0) {
                Log.d("readCalendarEvent: ", cursor.getString(1) + " :   " + convertToDate(cursor.getString(3)) + "--" + convertToDate(cursor.getString(4)));
                nameOfEvent.add(cursor.getString(1) + " :   " + convertToDate(cursor.getString(3)) + "--" + convertToDate(cursor.getString(4)));
                startDates.add(getDate(Long.parseLong(cursor.getString(3))));
                endDates.add(getDate(Long.parseLong(cursor.getString(4))));
                descriptions.add(cursor.getString(2));
                CNames[i] = cursor.getString(1);
                String eventID = cursor.getString(1);
                Calendar mycalendar = Calendar.getInstance();
                mycalendar.setTimeInMillis(Long.parseLong(cursor.getString(4)) - Long.parseLong(cursor.getString(3)));
                int hourDif = mycalendar.get(Calendar.HOUR);
                HashMap<String, Object> eventDetails = new HashMap<>();
                eventDetails.put("eventname", cursor.getString(1));
                eventDetails.put("estimatedtime", hourDif - 3);
                eventDetails.put("comments", cursor.getString(2) + " ");
                eventDetails.put("date", convertToDate(cursor.getString(3)));

                eventDetails.put("priority", "3");


                Log.d("readCalendarEvent", "!!!!!!!!!!!!!!!!: ");
                Log.d("readCalendarEvent", convertToDate(cursor.getString(4)) + "");
                Log.d("readCalendarEvent", convertToDate(cursor.getString(3)));
                Log.d("readCalendarEvent", "hourdif: " + (hourDif - 3) + "");

                dref.child("Users").child(userID).child("Events").child(eventID).setValue(eventDetails);

            }
            cursor.moveToNext();

        }
        return nameOfEvent;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String convertToDate(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH) + 1;
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int mHour = calendar.get(Calendar.HOUR);
        int mMinute = calendar.get(Calendar.MINUTE);

        return mDay + "/" + mMonth + "/" + mYear + "  " + mHour + ":" + mMinute;
    }
}
