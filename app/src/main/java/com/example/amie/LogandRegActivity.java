package com.example.amie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogandRegActivity extends AppCompatActivity {

    private Button mLogin, mRegister;

    // Override the onCreate method, called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_and_reg);

        // Initialize the login and registration buttons using their IDs from the layout file
        mLogin = (Button) findViewById(R.id.login);
        mRegister = (Button) findViewById(R.id.register);

        // Set an OnClickListener for the login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            // Define the onClick method, called when the login button is clicked
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogandRegActivity.this, AccessActivity.class);
                startActivity(i);
            }
        });

        // Set an OnClickListener for the registration button
        mRegister.setOnClickListener(new View.OnClickListener() {
            // Define the onClick method, called when the register button is clicked
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LogandRegActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }
}