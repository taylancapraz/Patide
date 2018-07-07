package com.wrexsoft.canturgut.patide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wrexsoft.canturgut.patide.R;

public class AskScreenForCallender extends AppCompatActivity {

    Button yes;
    Button no;

    SharedPreferences sett;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_screen_for_callender);

        sett = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        edit = sett.edit();

        getSupportActionBar().hide();

        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit.putBoolean("isImportCalendar", true);
                edit.apply();

                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit.putBoolean("isImportCalendar", false);
                edit.apply();

                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
