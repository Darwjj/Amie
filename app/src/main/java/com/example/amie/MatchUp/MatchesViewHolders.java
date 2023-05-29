package com.example.amie.MatchUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amie.Messages.MessageActivity;
import com.example.amie.R;

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId, mMatchName, mLastTimeStamp, mLastMessage, mInterest, mUniversity, mLocation, mProfile;
    public ImageView mNotificationDot;
    public ImageView mMatchImage;


    // Constructor to initialize views and set click listener
    public MatchesViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = (TextView) itemView.findViewById(R.id.Matchid);
        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);
        mLastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
        mLastTimeStamp = (TextView) itemView.findViewById(R.id.lastTimeStamp);

        mInterest = (TextView) itemView.findViewById(R.id.needid);
        mUniversity = (TextView) itemView.findViewById(R.id.giveid);
        mLocation = (TextView) itemView.findViewById(R.id.budgetid);
        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
        mProfile = (TextView) itemView.findViewById(R.id.profileid);
        mNotificationDot = (ImageView) itemView.findViewById(R.id.notification_dot);
    }

    // Handle item click, create intent and start ChatActivity
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), MessageActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        b.putString("matchName", mMatchName.getText().toString());
        b.putString("lastMessage", mLastMessage.getText().toString());
        b.putString("lastTimeStamp", mLastTimeStamp.getText().toString());
        b.putString("location", mLocation.getText().toString());
        b.putString("interest", mInterest.getText().toString());
        b.putString("university", mUniversity.getText().toString());
        b.putString("profile", mProfile.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
