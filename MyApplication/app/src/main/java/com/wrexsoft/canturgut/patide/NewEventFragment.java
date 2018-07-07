package com.wrexsoft.canturgut.patide;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

import static com.wrexsoft.canturgut.patide.AllEventsFragment.checkConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewEventFragment extends Fragment {

    View view;
    EditText mEventName;
    EditText mEstimatedTime;
    EditText mComments;
    Button ChooseDateButton;
    Button AddEventButton;
    TextView tvChooseDate;
    SeekBar mPriority;

    Button AddNotificationButton;
    Calendar notificationDate;

    DatabaseReference dref;

    String eventNameString;
    String estimatedTimeString;
    String commentsString;
    String dateString;
    String priorityString;

    SharedPreferences settings;
    String userID;
    private boolean createEventToken = false;


    public NewEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_event, container, false);

        dref = FirebaseDatabase.getInstance().getReference();
        settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        userID = settings.getString("FbUserId", "userID");

        mEventName = (EditText) view.findViewById(R.id.mEventName);
        mEstimatedTime = (EditText) view.findViewById(R.id.mEstimatedTime);
        mComments = (EditText) view.findViewById(R.id.mComments);

        ChooseDateButton = (Button) view.findViewById(R.id.chooseDateBtn);
        ChooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        mPriority = (SeekBar) view.findViewById(R.id.choosePrioritySB);
        AddEventButton = (Button) view.findViewById(R.id.addEventButton);

        notificationDate = Calendar.getInstance();
        AddNotificationButton = (Button) view.findViewById(R.id.chooseNotificationNewEvent);
        AddNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notDatePicker();
            }
        });

        AddEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainMenuActivity.isKeyboardActivated) {
                    hideSoftKeyboard(getActivity());
                }
                CreateNewEvent();

                if (createEventToken) {

                    GoToHome();

                }
            }
        });
        return view;
    }

    private void notDatePicker() {
        final Dialog notdatepicker = new Dialog(getContext());

        notdatepicker.setTitle("Pick a Date");

        notdatepicker.setContentView(R.layout.dialog_date);

        final DatePicker dPicker = (DatePicker) notdatepicker.findViewById(R.id.datePicker);
        dPicker.setMinDate(System.currentTimeMillis() - 1000);
        Button okBtn = (Button) notdatepicker.findViewById(R.id.btnDate);

        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                notificationDate.set(Calendar.YEAR, dPicker.getYear());
                notificationDate.set(Calendar.MONTH, dPicker.getMonth());
                notificationDate.set(Calendar.DAY_OF_MONTH, dPicker.getDayOfMonth());

                String day = "";
                if (dPicker.getDayOfMonth() < 10) {
                    day = "0";
                }
                String month = "";
                if (dPicker.getMonth() < 10) {
                    month = "0";
                }
                int monthInt = dPicker.getMonth() + 1;
                AddNotificationButton.setText(day + dPicker.getDayOfMonth() + "/" + month + Integer.toString(monthInt) + "/" + dPicker.getYear());
                notTimePicker();
                notdatepicker.dismiss();

            }
        });

        notdatepicker.show();
    }

    private void notTimePicker() {
        final Dialog nottimepicker = new Dialog(getContext());

        nottimepicker.setTitle("Pick a Time");

        nottimepicker.setContentView(R.layout.dialog_time);

        final TimePicker tPicker = (TimePicker) nottimepicker.findViewById(R.id.timePicker);
        Button backBtn = (Button) nottimepicker.findViewById(R.id.back2date);
        Button okBtn = (Button) nottimepicker.findViewById(R.id.timeOK);

        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String date = "" + AddNotificationButton.getText();

                notificationDate.set(Calendar.HOUR, tPicker.getHour());
                notificationDate.set(Calendar.MINUTE, tPicker.getMinute());
                notificationDate.set(Calendar.SECOND, 0);

                String hr = "";
                String min = "";
                if (Build.VERSION.SDK_INT >= 23) {
                    if (tPicker.getHour() < 10) {
                        hr = "0";
                    }
                    if (tPicker.getMinute() < 10) {
                        min = "0";
                    }
                    AddNotificationButton.setText(date + " " + hr + tPicker.getHour() + ":" + min + tPicker.getMinute());
                } else {
                    if (tPicker.getCurrentHour() < 10) {
                        hr = "0";
                    }

                    if (tPicker.getCurrentMinute() < 10) {
                        min = "0";
                    }
                    AddNotificationButton.setText(date + " " + hr + tPicker.getCurrentHour() + ":" + min + tPicker.getCurrentMinute());

                }

                nottimepicker.dismiss();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                nottimepicker.dismiss();
                notDatePicker();

            }
        });

        nottimepicker.show();
    }

    private void showDatePicker() {
        final Dialog datepicker = new Dialog(getContext());

        datepicker.setTitle("Pick a Date");

        datepicker.setContentView(R.layout.dialog_date);

        final DatePicker dPicker = (DatePicker) datepicker.findViewById(R.id.datePicker);
        dPicker.setMinDate(System.currentTimeMillis() - 1000);
        Button okBtn = (Button) datepicker.findViewById(R.id.btnDate);

        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String day = "";
                if (dPicker.getDayOfMonth() < 10) {
                    day = "0";
                }
                String month = "";
                if (dPicker.getMonth() < 10) {
                    month = "0";
                }
                int monthInt = dPicker.getMonth() + 1;
                ChooseDateButton.setText(day + dPicker.getDayOfMonth() + "/" + month + Integer.toString(monthInt) + "/" + dPicker.getYear());
                showTimePicker();
                datepicker.dismiss();

            }
        });

        datepicker.show();

    }

    private void showTimePicker() {

        final Dialog timepicker = new Dialog(getContext());

        timepicker.setTitle("Pick a Time");

        timepicker.setContentView(R.layout.dialog_time);

        final TimePicker tPicker = (TimePicker) timepicker.findViewById(R.id.timePicker);
        Button backBtn = (Button) timepicker.findViewById(R.id.back2date);
        Button okBtn = (Button) timepicker.findViewById(R.id.timeOK);

        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String date = "" + ChooseDateButton.getText();

                String hr = "";
                String min = "";
                if (Build.VERSION.SDK_INT >= 23) {
                    if (tPicker.getHour() < 10) {
                        hr = "0";
                    }
                    if (tPicker.getMinute() < 10) {
                        min = "0";
                    }
                    ChooseDateButton.setText(date + " " + hr + tPicker.getHour() + ":" + min + tPicker.getMinute());
                } else {
                    if (tPicker.getCurrentHour() < 10) {
                        hr = "0";
                    }

                    if (tPicker.getCurrentMinute() < 10) {
                        min = "0";
                    }
                    ChooseDateButton.setText(date + " " + hr + tPicker.getCurrentHour() + ":" + min + tPicker.getCurrentMinute());

                }

                timepicker.dismiss();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                timepicker.dismiss();
                showDatePicker();

            }
        });

        timepicker.show();
    }

    private void GoToHome() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("All Events");
        AllEventsFragment allEventsFragment = new AllEventsFragment();
        ft.replace(R.id.main_frame, allEventsFragment);
        ft.commit();
    }

    private void CreateNewEvent() {

        eventNameString = mEventName.getText().toString();
        estimatedTimeString = mEstimatedTime.getText().toString();
        commentsString = mComments.getText().toString();
        dateString = ChooseDateButton.getText().toString();
        priorityString = Integer.toString(mPriority.getProgress());

        Log.d("THISEVENTCREATE", "CreateNewEvent:" + eventNameString);


        if (eventNameString.equals("") || estimatedTimeString.equals("") || commentsString.equals("") || dateString.equals("") || priorityString.equals("")) {

            Toast.makeText(getContext(), "Complete All Fields and Try Again ", Toast.LENGTH_SHORT).show();

            Log.d("THISEVENTCREATE", "CreateNewEvent: ");

        } else {

            HashMap<String, Object> eventDetails = new HashMap<>();
            eventDetails.put("eventname", eventNameString);
            eventDetails.put("estimatedtime", estimatedTimeString);
            eventDetails.put("comments", commentsString);
            eventDetails.put("date", dateString);
            eventDetails.put("priority", priorityString);

            if (notificationDate != Calendar.getInstance()) {
                setNotification();
            }
            if (checkConnection(getActivity().getApplicationContext())) {
                dref.child("Users").child(userID).child("Events").push().setValue(eventDetails);
                Toast.makeText(getContext(), "Your New Event is Created!", Toast.LENGTH_SHORT).show();
            } else {
                String eventID = eventNameString + estimatedTimeString;

                MainMenuActivity.mydb.insertToKuyruk(eventID, "insert");
                MainMenuActivity.mydb.insertData(eventID, commentsString, dateString, estimatedTimeString, eventNameString, priorityString);
                //Toast.makeText(getContext(), "Your New Event will be created when internet connection is established!", Toast.LENGTH_SHORT).show();
            }

            createEventToken = true;

        }
    }

    private void setNotification() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        //Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.SECOND, 10);

        Intent intent = new Intent("CUSTOM_NOTIFICATION");
        PendingIntent broadcast = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationDate.getTimeInMillis(), broadcast);

        Log.e("Current Time:", Long.toString(notificationDate.getTimeInMillis()));

        Toast.makeText(getContext(), "Your notification has been set.", Toast.LENGTH_SHORT).show();

    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
