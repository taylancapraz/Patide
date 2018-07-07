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
import android.support.v7.app.ActionBar;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

import static com.wrexsoft.canturgut.patide.AllEventsFragment.checkConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailFragment extends Fragment {

    View view;
    String eventId;
    String fbuserId;
    DatabaseReference dref;
    SharedPreferences settings;

    EditText mEventName;
    EditText mEstimatedTime;
    EditText mComments;
    Button ChooseDateButton;
    Button mEditEvent;
    Button mDeleteEvent;
    SeekBar mPriority;

    Button AddNotificationButton;
    Calendar notificationDate;


    String eventNameString;
    String estimatedTimeString;
    String commentsString;
    String dateString;
    String priorityString;
    private boolean isAllFieldComlate = false;


    public EventDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_event_detail, container, false);

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

        notificationDate = Calendar.getInstance();
        AddNotificationButton = (Button) view.findViewById(R.id.chooseNotification);
        AddNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notDatePicker();
            }
        });


        mPriority = (SeekBar) view.findViewById(R.id.choosePrioritySB);
        mEditEvent = (Button) view.findViewById(R.id.editEventButton);
        mEditEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardAfterAction();

                editEvent();
                if (isAllFieldComlate) {
                    goToHome();

                }
            }
        });

        mDeleteEvent = (Button) view.findViewById(R.id.deleteEventButton);
        mDeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardAfterAction();
                areYouSure();

            }
        });

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle("Event Details");
        final Bundle bundle = this.getArguments();

        if (bundle != null) {
            eventId = bundle.getString("eventId", "0");
        }

        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        fbuserId = settings.getString("FbUserId", "userId");

        dref = FirebaseDatabase.getInstance().getReference();
        dref.child("Users").child(fbuserId).child("Events").child(eventId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    try {
                        mEventName.setText(dataSnapshot.child("eventname").getValue().toString());
                        mEstimatedTime.setText(dataSnapshot.child("estimatedtime").getValue().toString());
                        mComments.setText(dataSnapshot.child("comments").getValue().toString());
                        ChooseDateButton.setText(dataSnapshot.child("date").getValue().toString());
                        mPriority.setProgress(Integer.parseInt(dataSnapshot.child("priority").getValue().toString()));
                    } catch (NullPointerException e) {
                        Log.e("NPE", e.toString());

                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void areYouSure() {
        final Dialog ruSure = new Dialog(getContext());

        ruSure.setTitle("Patide");

        ruSure.setContentView(R.layout.dialog_sure);

        Button yesButton = (Button) ruSure.findViewById(R.id.yes);
        Button noButton = (Button) ruSure.findViewById(R.id.no);

        noButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ruSure.dismiss();

            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ruSure.dismiss();
                deleteEvent();
                goToHome();

            }
        });

        ruSure.show();
    }

    private void deleteEvent() {
        MainMenuActivity.mydb.removeEvent(eventId);
        if (checkConnection(getActivity().getApplicationContext())) {
            dref.child("Users").child(fbuserId).child("Events").child(eventId).removeValue();
        } else {
            MainMenuActivity.mydb.insertToKuyruk(eventId, "delete");
        }
    }

    private void goToHome() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("All Events");
        AllEventsFragment allEventsFragment = new AllEventsFragment();
        ft.replace(R.id.main_frame, allEventsFragment);
        ft.commit();
    }

    private void editEvent() {
        eventNameString = mEventName.getText().toString();
        estimatedTimeString = mEstimatedTime.getText().toString();
        commentsString = mComments.getText().toString();
        dateString = ChooseDateButton.getText().toString();
        priorityString = Integer.toString(mPriority.getProgress());

        if (eventNameString.equals("") || estimatedTimeString.equals("") || commentsString.equals("") || dateString.equals("") || priorityString.equals("")) {
            Toast.makeText(getContext(), "Complete All Fields and Try Again ", Toast.LENGTH_SHORT).show();
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

            MainMenuActivity.mydb.updateData(eventId, commentsString, dateString, estimatedTimeString, eventNameString, priorityString);
            if (checkConnection(getActivity().getApplicationContext())) {
                dref.child("Users").child(fbuserId).child("Events").child(eventId).setValue(eventDetails);
            } else {
                MainMenuActivity.mydb.insertToKuyruk(eventId, "update");
            }

            isAllFieldComlate = true;
            Toast.makeText(getContext(), "Your Event is Updated!", Toast.LENGTH_SHORT).show();

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
                    timepicker.dismiss();


                } else {
                    if (tPicker.getCurrentHour() < 10) {
                        hr = "0";
                    }

                    if (tPicker.getCurrentMinute() < 10) {
                        min = "0";
                    }
                    ChooseDateButton.setText(date + " " + hr + tPicker.getCurrentHour() + ":" + min + tPicker.getCurrentMinute());

                }
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


    public void hideKeyboardAfterAction() {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (MainMenuActivity.isKeyboardActivated) {
                        hideSoftKeyboard(getActivity());
                    }
                }
            }, 100);
        } catch (Exception e) {

        }

    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

}
