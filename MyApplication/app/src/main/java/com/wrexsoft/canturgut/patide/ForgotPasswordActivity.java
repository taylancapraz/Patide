package com.wrexsoft.canturgut.patide;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.wrexsoft.canturgut.patide.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button sendEmail;
    EditText emailField;
    TextView field;
    Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = (EditText) findViewById(R.id.forgot_password_field);
        field = (TextView) findViewById(R.id.check_email_field);

        field.setText("-");

        goBack = (Button) findViewById(R.id.go_back);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendEmail = (Button) findViewById(R.id.forget_password);
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if (emailField.getText() != null || emailField.getText().toString().equals("")) {

                    String emailAddress = emailField.getText().toString();

                    if (!emailAddress.isEmpty()) {


                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            field.setText(R.string.check_your_email_text);

                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }, 2000);


                                        } else {
                                            field.setText("Error with e-mail");
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}
