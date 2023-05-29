package com.example.amie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class LikeActivity extends AppCompatActivity {

    private static final String TAG = "BtnLikeActivtiy";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = LikeActivity.this;
    private ImageView like;

    // Override the onCreate method for the Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        // Call the setupTopNavigationView method to initialize the navigation view
        setupTopNavigationView();
        // Get the ImageView for the like button
        like = findViewById(R.id.like);

        // Get the profile URL from the intent
        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra("url");

        // Load the appropriate image into the ImageView
        switch (profileUrl) {
            case "default" :
                Glide.with(mContext).load(R.drawable.suelo).into(like);
                break;
            default:
                Glide.with(mContext).load(profileUrl).into(like);
                break;
        }

        // Start a new thread to pause for a short period and then return to the main activity
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent mainIntent = new Intent(LikeActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        }).start();
    }

    // Method for setting up the top navigation view
    private void setupTopNavigationView() {
        BottomNavigationViewEx tvEx = findViewById(R.id.topNavViewBar);
        NavigationActivity.setupTopNavigationView(tvEx);
        NavigationActivity.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // Method for handling the Like button click event
    public void LikeBtn(View view) {
    }
}