package com.example.amie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FloatScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view of this activity to the splash screen layout
        setContentView(R.layout.float_screen);
        // Create a new Handler object and post a delayed Runnable
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(FloatScreen.this, LogandRegActivity.class);
                // Start the Choose_Login_And_Reg activity using the Intent
                startActivity(i);
                // Close the SplashScreenActivity
                finish();
            }
            // Set the delay for the splash screen to 2 seconds (2000 milliseconds)
        },2000);

    }
}