package com.example.amie;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.amie.MatchUp.MatchesActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class NavigationActivity {

    // Constant for logging purposes
    private static final String TAG = "TopNavigationViewHelper";

    // Method to set up the top navigation view
    public static void setupTopNavigationView(BottomNavigationViewEx tv) {
        Log.d(TAG, "setupTopNavigationView: setting up navigationview");
    }

    // Method to enable navigation functionality for the given BottomNavigationViewEx
    public static void enableNavigation(final Context context, BottomNavigationViewEx view) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_profile:
                        // Create a new intent to launch the SettingsActivity
                        Intent i = new Intent(context, SettingsActivity.class);
                        context.startActivity(i);
                        break;
                    // Case for when the matched icon is selected
                    case R.id.ic_matched:
                        Intent i1 = new Intent(context, MatchesActivity.class);
                        context.startActivity(i1);
                        break;
                }
                // Return false to indicate that the event was not consumed
                return false;
            }
        });
    }
}
