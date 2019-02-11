package com.henryhilton.internationalstudentapp;

/**
 * Created by Henry on 12/5/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;


public class SignInExample extends Activity {

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        ImageView imageView = findViewById(R.id.logoImageView);
        mLoginButton = findViewById(R.id.signinButton);
        mEmailEditText = findViewById(R.id.emailEditText);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        imageView.setImageResource(R.drawable.islogo);

        mAuth = FirebaseAuth.getInstance();

//        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String autoEmail =  preferences.getString("Email", null);
//        String autoPassword = preferences.getString("Password", null);
//
//        if (autoHandle != null && autoPassword != null){
//            sendAutoLogin(autoHandle,autoPassword);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            launchLoginIntent();
        }
    }

    // clears any saved data from shared prefs and send user to create user activity
    public void createAccount(View v){
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this, CreateUser.class);
        startActivity(intent);
        finish();
    }

    public void clear(View v){
        mPasswordEditText.setText("");
        mEmailEditText.setText("");
    }

    // fires a get call to check sign in with inputted details
    public void login(View v){

        String email = mEmailEditText.getText().toString();
        String password =  mPasswordEditText.getText().toString();

        if (email.length()!=0 && password.length()!=0) {
            Log.d("email", email);
            Log.d("password",password);

            firebaseLogin(email,password);
        }
        else Toast.makeText(this, "Make sure you add something to both fields!", Toast.LENGTH_SHORT).show();
    }

    public void launchLoginIntent(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
         finish();
    }

    public void firebaseLogin(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, launch login intent
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            launchLoginIntent();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}