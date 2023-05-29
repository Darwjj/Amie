package com.example.amie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MembershipActivity extends AppCompatActivity {


    private ImageButton mBack;
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);


        mBack = findViewById(R.id.settingsBack);
        spinner = (ProgressBar) findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);

        // Set the text and HTML link for Membership Tier 1
        TextView textview = (TextView) findViewById(R.id.textView3);
        textview.setText(Html.fromHtml("Click this button to " +
                "<a href = 'https://docs.google.com/forms/d/e/1FAIpQLSdY0lt2B0Xa2MBIFVUCq01tHRCLp_rFgXfI6WUvzVx-EH7Ptw/viewform?usp=sf_link'> Membership Tier 1</a>"));
        textview.setClickable(true);
        textview.setMovementMethod(LinkMovementMethod.getInstance());

        // Set the text and HTML link for Membership Tier 2
        TextView textview2 = (TextView) findViewById(R.id.textView4);
        textview2.setText(Html.fromHtml("Click this button to " +
                "<a href = 'https://docs.google.com/forms/d/e/1FAIpQLSc3CDsUE3mMw_bSymVB7sLeEV7-5us_xOzXoNhqcIVcsN7Q6w/viewform?usp=sf_link'> Membership Tier 2</a>"));
        textview2.setClickable(true);
        textview2.setMovementMethod(LinkMovementMethod.getInstance());

        // Set the text and HTML link for Membership Tier 3
        TextView textview3 = (TextView) findViewById(R.id.textView5);
        textview3.setText(Html.fromHtml("Click this button to " +
                "<a href = 'https://docs.google.com/forms/d/e/1FAIpQLSe0LN7yIBbMkJh1dTdwNWesP5YWh4rozlXzkwZTtnVycPXEwA/viewform?usp=sf_link'> Membership Tier 3</a>"));
        textview3.setClickable(true);
        textview3.setMovementMethod(LinkMovementMethod.getInstance());



        // Set an OnClickListener for the back button
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MembershipActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        // Find the toolbar by its ID and set it as the support action bar
        Toolbar toolbar = findViewById(R.id.settings_toolbartag);
        setSupportActionBar(toolbar);
    }
}