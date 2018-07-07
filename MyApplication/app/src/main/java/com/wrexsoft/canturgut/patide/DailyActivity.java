package com.wrexsoft.canturgut.patide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DailyActivity extends AppCompatActivity {

    EditText mLeisure;
    EditText mWork;
    EditText mStudy;
    Button mStart;
    FirebaseUser user;
    String fbuserId;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        mLeisure = (EditText) findViewById(R.id.mLeisure);
        mWork = (EditText) findViewById(R.id.mWork);
        mStudy = (EditText) findViewById(R.id.mStudy);
        mStart = (Button) findViewById(R.id.mStart);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApp();
            }
        });

        dref = FirebaseDatabase.getInstance().getReference();
        settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        fbuserId = settings.getString("FbUserId", "userID");

    }

    private void startApp() {

        String leisure = mLeisure.getText().toString();
        String work = mWork.getText().toString();
        String study = mStudy.getText().toString();

        dref.child("Users").child(fbuserId).child("Daily").child("leisure").setValue(leisure);
        dref.child("Users").child(fbuserId).child("Daily").child("work").setValue(work);
        dref.child("Users").child(fbuserId).child("Daily").child("study").setValue(study);

        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);
        finish();

    }
}
