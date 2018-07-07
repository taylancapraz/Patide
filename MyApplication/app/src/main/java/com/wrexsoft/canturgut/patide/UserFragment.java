package com.wrexsoft.canturgut.patide;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    View view;
    TextView mUserName;
    Button mSignout;
    Switch cllanderSwitch;

    private Button mEdit;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mLeisure;
    private EditText mWork;
    private EditText mStudy;
    private Boolean editable;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private String fbuserId;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private DatabaseReference dref;

    public UserFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user, container, false);

        settings = PreferenceManager.getDefaultSharedPreferences(getContext());

        mUserName = (TextView) view.findViewById(R.id.username);
        mUserName.setText(settings.getString("name", "name") + " " + (settings.getString("lastname", "lastname")));
        mEmail = (EditText) view.findViewById(R.id.etEmail);
        mEmail.setText("" + settings.getString("email", "email"));
        mEmail.setTag(mEmail.getKeyListener());
        mEmail.setKeyListener(null);

        mPassword = (EditText) view.findViewById(R.id.etPassword);
        mPassword.setTag(mPassword.getKeyListener());
        mPassword.setKeyListener(null);

        mPassword.setVisibility(View.GONE);

        mLeisure = (EditText) view.findViewById(R.id.etLeisure);
        mLeisure.setText("" + settings.getString("leisure", "0"));
        mLeisure.setTag(mLeisure.getKeyListener());
        mLeisure.setKeyListener(null);

        mWork = (EditText) view.findViewById(R.id.etWork);
        mWork.setText("" + settings.getString("work", "0"));
        mWork.setTag(mWork.getKeyListener());
        mWork.setKeyListener(null);

        mStudy = (EditText) view.findViewById(R.id.etStudy);
        mStudy.setText("" + settings.getString("study", "0"));
        mStudy.setTag(mStudy.getKeyListener());
        mStudy.setKeyListener(null);

        dref = FirebaseDatabase.getInstance().getReference();

        editable = false;
        mEdit = (Button) view.findViewById(R.id.edit_button);

        cllanderSwitch = (Switch) view.findViewById(R.id.callender_acticate);

        boolean calHolder = false;

        if (settings.getBoolean("isImportCalendar", calHolder)) {
            cllanderSwitch.setChecked(true);

        } else {
            cllanderSwitch.setChecked(false);
        }

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editable) {
                    editable = true;
                    mPassword.setVisibility(View.VISIBLE);
                    mEmail.setKeyListener((KeyListener) mEmail.getTag());
                    mPassword.setKeyListener((KeyListener) mPassword.getTag());
                    mLeisure.setKeyListener((KeyListener) mLeisure.getTag());
                    mWork.setKeyListener((KeyListener) mWork.getTag());
                    mStudy.setKeyListener((KeyListener) mStudy.getTag());
                    mEdit.setText(R.string.userscreen_button_done);

                } else {

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (MainMenuActivity.isKeyboardActivated) {
                                hideSoftKeyboard(getActivity());
                            }
                        }
                    }, 100);

                    boolean passwordFieldCheck = mPassword.getText().toString().equals("") || mPassword.getText().toString() == null;

                    if (!user.getEmail().toString().equals(mEmail.getText().toString())) {
                        if (isEmailValid(mEmail.getText().toString())) {
                            user.updateEmail(mEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Log.d("CHECKEMAIL", "User email address not updated: " + mEmail.getText().toString());
                                                Toast.makeText(getContext(), "E-Mail Changed", Toast.LENGTH_SHORT).show();

                                                dref.child("Users").child(fbuserId).child("UserData").child("email").setValue(mEmail.getText().toString());

                                                editor = settings.edit();
                                                editor.putString("email", mEmail.getText().toString());
                                                editor.apply();

                                            } else {

                                                Log.d("CHECKEMAIL", "User email address not updated: " + mEmail.getText().toString());
                                                Toast.makeText(getContext(), "Error: E-Mail Not Changed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }

                    if (!passwordFieldCheck) {

                        user.updatePassword(mPassword.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();

                                        } else {

                                            Toast.makeText(getContext(), "ERROR: Password Not Changed", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                    }


                    editable = false;
                    mPassword.setVisibility(View.GONE);
                    mEmail.setKeyListener(null);
                    mPassword.setKeyListener(null);
                    mLeisure.setKeyListener(null);
                    mWork.setKeyListener(null);
                    mStudy.setKeyListener(null);
                    mEdit.setText(R.string.userscreen_button_edit);

                    String leisure = mLeisure.getText().toString();
                    String work = mWork.getText().toString();
                    String study = mStudy.getText().toString();

                    dref.child("Users").child(fbuserId).child("Daily").child("leisure").setValue(leisure);
                    dref.child("Users").child(fbuserId).child("Daily").child("work").setValue(work);
                    dref.child("Users").child(fbuserId).child("Daily").child("study").setValue(study);

                    editor = settings.edit();
                    editor.putString("leisure", leisure);
                    editor.putString("work", work);
                    editor.putString("study", study);

                    if (cllanderSwitch.isChecked()) {
                        editor.putBoolean("isImportCalendar", true);
                    } else {
                        editor.putBoolean("isImportCalendar", false);
                    }

                    editor.apply();
                }
            }
        });

        mSignout = (Button) view.findViewById(R.id.signout_button);
        mSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                areYouSure();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "Firebase Auth";

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    fbuserId = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + fbuserId);
                } else {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public boolean isEmailValid(String email) {

        if (email.contains("@") && email.contains(".")) {
            if (email.length() >= 6) {
                return true;
            }
        }
        return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
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
                MainMenuActivity.mydb.dropAllTables();
                mAuth.signOut();
                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e) {

                }
                editor = settings.edit();
                editor.clear();
                editor.apply();

            }
        });

        ruSure.show();
    }
}
