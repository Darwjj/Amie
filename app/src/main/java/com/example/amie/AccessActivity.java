package com.example.amie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AccessActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar spinner;
    private Button mLogin;
    private EditText mEmail, mPassword;
    private TextView mForgetPassword;
    private boolean loginBtnClicked;
    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access);

        loginBtnClicked = false;
        spinner = (ProgressBar) findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        mLogin = (Button) findViewById(R.id.login);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mForgetPassword = (TextView) findViewById(R.id.forgetPasswordButton);

        // Set OnClickListener for the login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtnClicked = true;
                spinner.setVisibility(View.VISIBLE);
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                // Check if fields are empty and show error message
                if (isStringNull(email) ||  isStringNull(password)) {
                    Toast.makeText(AccessActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    // Sign in with email and password using FirebaseAuth
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(AccessActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(AccessActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }else {
                                        // Check if email is verified
                                        if (mAuth.getCurrentUser().isEmailVerified()) {
                                            Intent i = new Intent(AccessActivity.this, MainActivity.class);
                                            startActivity(i);
                                            finish();
                                            return;
                                        }else {
                                            Toast.makeText(AccessActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
                spinner.setVisibility(View.GONE);
            }
        });

        // Set OnClickListener for the forgot password button
        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                Intent i = new Intent(AccessActivity.this, PasswordActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });

//        // Code commented out to disable auto-login
//        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                if (user != null && user.isEmailVerified() && !loginBtnClicked) {
//                    spinner.setVisibility(View.VISIBLE);
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(i);
//                    finish();
//                    spinner.setVisibility(View.GONE);
//                    return;
//                }
//            }
//        };
    }

    // Helper method to check if a string is empty
    private boolean isStringNull(String email) {
        return email.equals("");
    }
    // Start method
    @Override
    protected void onStart() {
        super.onStart();
      //  FirebaseAuth.getInstance().signOut();

    }

    // Stop method
    @Override
    protected void onStop() {
        super.onStop();
     //       FirebaseAuth.getInstance().signOut();
      //  mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    // Back method
    @Override
    public void onBackPressed() {
        Intent i = new Intent(AccessActivity.this, LogandRegActivity.class);
        startActivity(i);
        finish();
        return;
    }
}
