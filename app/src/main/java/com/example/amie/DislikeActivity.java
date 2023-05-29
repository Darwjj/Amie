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

public class DislikeActivity extends AppCompatActivity {

    private static final String TAG = "BtnDislikeActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = DislikeActivity.this;
    private ImageView dislike;

    // onCreate method called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dislike);

        // Set up the top navigation view
        setupTopNavigationView();
        // Get reference to the ImageView for the dislike button
        dislike = findViewById(R.id.dislike);

        // Get the intent and extract the profile URL
        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra("url");

        switch (profileUrl) {
            case "default" :
                Glide.with(mContext).load(R.drawable.suelo).into(dislike);
                break;
            default:
                Glide.with(mContext).load(profileUrl).into(dislike);
                break;
        }

        // Start a new thread to pause and then return to the MainActivity
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent mainIntent = new Intent(DislikeActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        }).start();
    }

    // Method to set up the top navigation view
    private void setupTopNavigationView() {
        BottomNavigationViewEx tvEx = findViewById(R.id.topNavViewBar);
        NavigationActivity.setupTopNavigationView(tvEx);
        NavigationActivity.enableNavigation(mContext, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // Method for the Like button onClick event
    public void LikeBtn(View view) {
    }
}