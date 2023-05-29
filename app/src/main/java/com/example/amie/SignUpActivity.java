package com.example.amie;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amie.SwipeCards.cards;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private Button mRegister;
    private ProgressBar spinner;
    private EditText mEmail, mPassword, mName, mBudget, mPhone;

    private RadioGroup mRadioGroup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        spinner = (ProgressBar) findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);
        TextView  existing = (TextView) findViewById(R.id.existing);
        mAuth = FirebaseAuth.getInstance();
        // Firebase authentication state listener
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                spinner.setVisibility(View.VISIBLE);
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.isEmailVerified()) {
                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);//later we create
                    startActivity(i);
                    finish();
                    spinner.setVisibility(View.GONE);
                    return;
                }
                spinner.setVisibility(View.GONE);
            }
        };

        // Click listener for existing users
        existing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });
        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox1);
        TextView textview = (TextView) findViewById(R.id.TextView2);

        mPhone = (EditText) findViewById(R.id.phone_number);

        // Set text and link for Terms and Conditions
        checkBox.setText("");
        textview.setText(Html.fromHtml("I have read and agree to the " +
                "<a href = 'https://corecomputersciences.blogspot.com/2022/12/privacy-policy.html'> Terms & Conditions</a>"));
        textview.setClickable(true);
        textview.setMovementMethod(LinkMovementMethod.getInstance());

        // Register button click listener
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);

                // Get user input
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final Boolean tnc = checkBox.isChecked();
                final String phone = mPhone.getText().toString();

                // Validate inputs and create a new user
                if (checkInputs(email, name, password, tnc, phone)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }else {
                                // Send email verification
                                mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Show success message and add user data to the database
                                            Toast.makeText(SignUpActivity.this, "Registration successfully." +
                                                    " Please check your email for verification. ", Toast.LENGTH_SHORT).show();
                                            String userId = mAuth.getCurrentUser().getUid();
                                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference()
                                                    .child("Users").child(userId);

                                          cards item = new cards(userId,name,"default"," "," "," ");

                                          currentUserDb.setValue(item);

                                            // Clear input fields and navigate to login screen
                                            mEmail.setText("");
                                            mName.setText("");
                                            mPassword.setText("");
                                            mPhone.setText("");
                                            Intent i = new Intent(SignUpActivity.this, LogandRegActivity.class);
                                            startActivity(i);
                                            finish();
                                            return;
                                        } else {
                                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
                spinner.setVisibility(View.GONE);
            }
        });
    }


    // Check inputs and validate them
    private boolean checkInputs(String email, String username, String password, Boolean tnc, String phone) {
        // Check if all fields are filled
        if (email.equals("") || username.equals("") || password.equals("") || phone.equals("")) {
            Toast.makeText(this, "All fields must be filled out", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check email format
        if (!email.matches(emailPattern)) {
            mEmail.setError("Invalid Email");
            Toast.makeText(this, "Invaild email address, enter valid email id and click on confirm", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if Terms and Conditions are accepted
        if (!tnc) {
            Toast.makeText(this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Validate P-number
        if (!Pattern.matches("[a-zA-Z] + ", phone)) {
             if(phone.length() > 9 && phone.length() <=13){

             }
             else{
                 mPhone.setError("Invalid P-Number");
                 return false;
             }

        }
        else
        {
            mPhone.setError("Invalid P-Number, try again. It must be between 9 to 13 digits");
        }

        return true;
    }

    // Start method
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    // Stop method
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    // Back method
    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignUpActivity.this, LogandRegActivity.class);
        startActivity(i);
        finish();
    }
}
