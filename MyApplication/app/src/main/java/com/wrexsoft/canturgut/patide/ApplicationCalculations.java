package com.wrexsoft.canturgut.patide;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

/**
 * Created by canta on 5/24/2017.
 */

public class ApplicationCalculations {
    public static String[] listOfEventIDs = new String[100];
    public static String[] listOfEventNames = new String[100];
    public static String[] listOfEventEstimatedTimes = new String[100];
    public static Date[] listOfEventDate = new Date[100];
    public static String[] listOfEventTimeLeft = new String[100];
    public static String[] listOfEventPriority = new String[100];

    private static Context context;

    public ApplicationCalculations(Context context) {
        this.context = context;
    }

    public static int size = 0;
    private static SharedPreferences settings;


    public static void fillArray() {
        Cursor listCursor = MainMenuActivity.mydb.getSQLiteData();
        int numOfEvent = listCursor.getCount();
        if (numOfEvent == 0) {
            Log.d("databaseInsert", "Cursor Null");
        } else {
            size = 0;
            StringBuffer buffer = new StringBuffer();
            while (listCursor.moveToNext()) {
                listOfEventIDs[size] = listCursor.getString(1);
                listOfEventNames[size] = listCursor.getString(5);
                listOfEventEstimatedTimes[size] = listCursor.getString(4);
                listOfEventDate[size] = getTime(listCursor.getString(3));
                listOfEventTimeLeft[size] = listCursor.getString(1);
                listOfEventPriority[size] = listCursor.getString(6);
                size++;
            }
            sortArray();
            calculate();
        }
    }

    private static void calculate() {
        settings = PreferenceManager.getDefaultSharedPreferences(context);

        String leisure = settings.getString("leisure", "0");
        String work = settings.getString("work", "0");
        String study = settings.getString("study", "0");

        Date currentTime = new Date();
        for (int i = 0; i < size; i++) {
            int otherEvents = 0;
            for (int k = 0; k < i; k++) {
                otherEvents = otherEvents + Integer.parseInt(listOfEventEstimatedTimes[k]);
            }
            Log.d("calculations", "--------------------------------");
            Log.d("calculations", "calculate: " + listOfEventNames[i]);
            Log.d("calculations", "today: " + currentTime);
            Log.d("calculations", "event time: " + listOfEventDate[i]);
            long diff = listOfEventDate[i].getTime() - currentTime.getTime();
            long diffDays = diff / (6 * 60 * 60 * 1000);
            Log.d("calculations", "Day difference" + diffDays);
            Log.d("calculations", "Calculate: " + otherEvents + " + " + diffDays + "*" + (Integer.parseInt(leisure) + Integer.parseInt(work) + Integer.parseInt(study)) + "/4");
            otherEvents = otherEvents + (int) diffDays * ((Integer.parseInt(leisure) + Integer.parseInt(work) + Integer.parseInt(study)) / 4);
            Log.d("calculations", "Result is " + otherEvents);
            diff = listOfEventDate[i].getTime() - currentTime.getTime();
            diff = diff - (otherEvents * 60 * 60 * 1000);
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            diffDays = diff / (24 * 60 * 60 * 1000);
            String showString = "";
            int controller = 0;
            if (diffDays > 0) {
                if (controller == 1) {
                    showString = showString + ", ";
                }
                ;
                controller = 1;
                showString = showString + diffDays + " days";
            } else {
                controller = 0;
            }
            if (diffHours > 0) {
                if (controller == 1) {
                    showString = showString + ", ";
                }
                ;
                controller = 1;
                showString = showString + diffHours + " hours";
            }
            {
                controller = 0;
            }
            if (diffMinutes > 0 && diffDays < 20) {
                if (controller == 1) {
                    showString = showString + ", ";
                }
                ;
                controller = 1;
                showString = showString + ", " + diffMinutes + " minutes";
            }
            {
                controller = 0;
            }
            if (showString.equals("")) {
                showString = showString + "This Event is Passed";
            }
            listOfEventTimeLeft[i] = showString;
        }
    }

    public static void sortArray() {
        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size - i; j++) {
                if (listOfEventDate[j].compareTo(listOfEventDate[j + 1]) > 0) {
                    replaceitems(j);
                }
            }
        }
    }

    public static void sortbyName() {

        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size - i; j++) {
                if (listOfEventNames[j].compareTo(listOfEventNames[j + 1]) > 0) {
                    replaceitems(j);
                }
            }
        }
    }

    public static void sortbyPriority() {

        for (int i = 1; i < size; i++) {
            for (int j = 0; j < size - i; j++) {
                if (listOfEventPriority[j].compareTo(listOfEventPriority[j + 1]) < 0) {
                    replaceitems(j);
                }
            }
        }
    }

    private static void replaceitems(int j) {
        String tempID;
        String tempName;
        String tempEst;
        Date tempDate;
        String tempPri;
        String tempTimeLeft;
        tempID = listOfEventIDs[j];
        listOfEventIDs[j] = listOfEventIDs[j + 1];
        listOfEventIDs[j + 1] = tempID;

        tempName = listOfEventNames[j];
        listOfEventNames[j] = listOfEventNames[j + 1];
        listOfEventNames[j + 1] = tempName;

        tempEst = listOfEventEstimatedTimes[j];
        listOfEventEstimatedTimes[j] = listOfEventEstimatedTimes[j + 1];
        listOfEventEstimatedTimes[j + 1] = tempEst;

        tempDate = listOfEventDate[j];
        listOfEventDate[j] = listOfEventDate[j + 1];
        listOfEventDate[j + 1] = tempDate;

        tempPri = listOfEventPriority[j];
        listOfEventPriority[j] = listOfEventPriority[j + 1];
        listOfEventPriority[j + 1] = tempPri;

        tempTimeLeft = listOfEventTimeLeft[j];
        listOfEventTimeLeft[j] = listOfEventTimeLeft[j + 1];
        listOfEventTimeLeft[j + 1] = tempTimeLeft;
    }

    public static Date getTime(String date) {
        String[] splited = date.split("\\s+");
        String s1 = splited[0];
        String[] dateValues = s1.split("/");
        String s2 = splited[1];
        String[] timeValues = s2.split(":");
        Date mydate = new Date();

        mydate.setYear(Integer.parseInt(dateValues[2]) - 1900);
        mydate.setMonth(Integer.parseInt(dateValues[1]) - 1);
        mydate.setDate(Integer.parseInt(dateValues[0]));
        mydate.setHours(Integer.parseInt(timeValues[0]));
        mydate.setMinutes(Integer.parseInt(timeValues[1]));
        return mydate;
    }

    public static String[] getListOfEventIDs() {
        return listOfEventIDs;
    }

    public static void setListOfEventIDs(String[] listOfEventIDs) {
        ApplicationCalculations.listOfEventIDs = listOfEventIDs;
    }

    public static String[] getListOfEventNames() {
        return listOfEventNames;
    }

    public static void setListOfEventNames(String[] listOfEventNames) {
        ApplicationCalculations.listOfEventNames = listOfEventNames;
    }

    public static String[] getListOfEventTimeLeft() {
        return listOfEventTimeLeft;
    }

    public static void setListOfEventTimeLeft(String[] listOfEventTimeLeft) {
        ApplicationCalculations.listOfEventTimeLeft = listOfEventTimeLeft;
    }

    public static String[] getListOfEventPriority() {
        return listOfEventPriority;
    }

    public static void setListOfEventPriority(String[] listOfEventPriority) {
        ApplicationCalculations.listOfEventPriority = listOfEventPriority;
    }

    public static int getSize() {
        return size;
    }
}
