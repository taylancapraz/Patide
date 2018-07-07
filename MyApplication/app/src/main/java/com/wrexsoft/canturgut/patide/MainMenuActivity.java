package com.wrexsoft.canturgut.patide;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainMenuActivity extends AppCompatActivity {

    DatabaseReference dref;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    ActionBar ab;
    static DatabaseHelper mydb;
    static BottomNavigationView navigation;
    static boolean isKeyboardActivated = false;

    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    ab.setTitle("All Events");
                    AllEventsFragment allEventsFragment = new AllEventsFragment();
                    ft.replace(R.id.main_frame, allEventsFragment);
                    ft.commit();
                    return true;
                case R.id.navigation_dashboard:
                    ab.setTitle("Create Event");
                    NewEventFragment newEventFragment = new NewEventFragment();
                    ft.replace(R.id.main_frame, newEventFragment);
                    ft.commit();
                    return true;
                case R.id.navigation_notifications:
                    ab.setTitle("User Settings");
                    UserFragment userFragment = new UserFragment();
                    ft.replace(R.id.main_frame, userFragment);
                    ft.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mydb = new DatabaseHelper(this);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        ab = getSupportActionBar();

        final Context context = this.getApplicationContext();

        final View activityRootView = findViewById(R.id.container);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(context, 200)) { // if more than 200 dp, it's probably a keyboard...

                    navigation.setVisibility(View.GONE);
                    Log.d("KEYBOARD **", "KEYBOARD IS OPEN");
                    isKeyboardActivated = true;

                } else {
                    navigation.setVisibility(View.VISIBLE);
                    Log.d("KEYBOARD **", "KEYBOARD IS NOT OPEN");
                    isKeyboardActivated = false;
                }
            }
        });

        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String userID = settings.getString("FbUserId", "userID");

        dref = FirebaseDatabase.getInstance().getReference();
        dref.child("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //     String EMAIL = dataSnapshot.child("UserData").child("email").getValue().toString();

                for (DataSnapshot data : dataSnapshot.child("UserData").getChildren()) {
                    editor = settings.edit();
                    if (Objects.equals(data.getKey(), "name")) {
                        String mName = data.getValue().toString();
                        editor.putString("name", mName);
                        Log.d("mName", mName);
                        Toast.makeText(getBaseContext(), "Welcome " + mName, Toast.LENGTH_SHORT).show();
                    }
                    if (Objects.equals(data.getKey(), "lastname")) {
                        String mLastName = data.getValue().toString();
                        editor.putString("lastname", mLastName);
                        Log.d("mLastName", mLastName);
                    }
                    if (Objects.equals(data.getKey(), "email")) {
                        String mEmail = data.getValue().toString();
                        editor.putString("email", mEmail);
                        Log.d("mEmail", mEmail);
                    }
                    editor.apply();
                }


                for (DataSnapshot data : dataSnapshot.child("Daily").getChildren()) {
                    editor = settings.edit();
                    if (Objects.equals(data.getKey(), "leisure")) {
                        String mLeisure = data.getValue().toString();
                        editor.putString("leisure", mLeisure);
                        Log.d("leisure", mLeisure);
                    }
                    if (Objects.equals(data.getKey(), "work")) {
                        String mWork = data.getValue().toString();
                        editor.putString("work", mWork);
                        Log.d("work", mWork);
                    }
                    if (Objects.equals(data.getKey(), "study")) {
                        String mStudy = data.getValue().toString();
                        editor.putString("study", mStudy);
                        Log.d("study", mStudy);
                    }
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ab.setTitle("All Events");
        AllEventsFragment allEventsFragment = new AllEventsFragment();
        ft.replace(R.id.main_frame, allEventsFragment);
        ft.commit();
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}
