package com.example.amie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class PasswordActivity extends AppCompatActivity {

    private Button mForgotPasswordButton;
    private EditText mEmail;
    private FirebaseAuth mAuth;
    private int flag;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();
        flag = 0;
        // Find and assign UI elements by ID
        mForgotPasswordButton = (Button) findViewById(R.id.resetPasswordButton);
        mEmail = (EditText) findViewById(R.id.resetPasswordEmail);

        // Set onClickListener for the forgot password button
        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                // Check if the email field is empty
                if (email.equals("")) {
                    Toast.makeText(PasswordActivity.this, "Email is empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the email id is valid
                if (!email.matches(emailPattern)) {
                    Toast.makeText(PasswordActivity.this, "Invaild email address, enter valid email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch sign-in methods for the entered email and send password reset email
                mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        flag = 1;
                        mAuth.sendPasswordResetEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PasswordActivity.this, "Password reset instructions send to your email", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(PasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                if (flag == 0)
                    Toast.makeText(PasswordActivity.this, "Email address not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Override onBackPressed method to navigate back to LoginActivity
    @Override
    public void onBackPressed() {
        Intent btnClick = new Intent(PasswordActivity.this, AccessActivity.class);
        startActivity(btnClick);
        super.onBackPressed();
        finish();
        return;
    }
}