package com.example.amie.MatchUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;

import com.example.amie.MainActivity;
import com.example.amie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private ImageButton mBack;
    private DatabaseReference current;
    private ValueEventListener listen;
    private HashMap<String, Integer> mList = new HashMap<>();
    private String currentUserId, mLastTimeStamp, mLastMessage, lastSeen;
    DatabaseReference mCurrUserIdInsideMatchConnections, mCheckLastSeen;

    // onCreate method for initializing the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        mBack = findViewById(R.id.matchesBack);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initializing RecyclerView and its components
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        // Setting onClickListener for the back button
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        getUserMatchId();
        mLastMessage = mLastTimeStamp = lastSeen = "";
    }

    // onPause method for handling activity's paused state
    @Override
    protected void onPause() {
        super.onPause();
    }

    // onStop method for handling activity's stopped state
    @Override
    protected void onStop() {
        super.onStop();
    }

    // Method to get the last message information
    private void getLastMessageInfo(DatabaseReference userDb) {
        mCurrUserIdInsideMatchConnections = userDb.child("connections").child("matches").child(currentUserId);

        mCurrUserIdInsideMatchConnections.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("lastMessage").getValue() != null && dataSnapshot.child("lastTimeStamp")
                            .getValue() != null && dataSnapshot.child("lastSend").getValue() != null) {
                        mLastMessage = dataSnapshot.child("lastMessage").getValue().toString();
                        mLastTimeStamp = dataSnapshot.child("lastTimeStamp").getValue().toString();
                        lastSeen = dataSnapshot.child("lastSend").getValue().toString();
                    }
                    else {
                        mLastMessage = "Start Chatting one";
                        mLastTimeStamp = " ";
                        lastSeen = "true";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Method to get user match IDs
    private void getUserMatchId() {
        Query sortedMatchesByLastTimeStamp = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(currentUserId).child("connections").child("yeps")
                .orderByChild("lastTimeStamp");

        sortedMatchesByLastTimeStamp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        FetchMatchInformation(match.getKey(), match.child("Chatid").toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Method to fetch match information
    private void FetchMatchInformation(final String key, final String chatid) {
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        getLastMessageInfo(userDb);

        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImg = "";
                    String interest = "";
                    String university = "";
                    String location = "";
                    final String lastMessage = "";
                    String lastTimeStamp = "";

                    // Extracting user information from dataSnapshot
                    if (dataSnapshot.child("name").getValue() != null) {
                        name = dataSnapshot.child("name").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImg").getValue() != null) {
                        profileImg = dataSnapshot.child("profileImg").getValue().toString();
                    }
                    if (dataSnapshot.child("interest").getValue() != null) {
                        interest = dataSnapshot.child("interest").getValue().toString() ;
                    }
                    if (dataSnapshot.child("university").getValue() != null) {
                        university = dataSnapshot.child("university").getValue().toString();
                    }
                    if (dataSnapshot.child("location").getValue().toString() != null) {
                        location = dataSnapshot.child("location").getValue().toString();
                    }

                    String milliSec = mLastTimeStamp;
                    Long now;

                    try {
                        now = Long.parseLong(milliSec);
                        lastTimeStamp = convertMilliToRelative(now);
                        String[] arrOfStr = lastTimeStamp.split(",");
                        mLastTimeStamp = arrOfStr[0];
                    }catch (Exception e){}

                    // Creating a MatchesObject and updating RecyclerView
                    MatchesObject obj = new MatchesObject(userId, name, profileImg, interest, university, location
                            , mLastMessage, mLastTimeStamp, chatid, lastMessage);
                    if (mList.containsKey(chatid)) {
                        int key = mList.get(chatid);
                        resultsMatches.set(resultsMatches.size() - key , obj);
                    }
                    else {
                        resultsMatches.add(0, obj);
                        mList.put(chatid, resultsMatches.size());
                    }
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Method to convert milliseconds to a relative time string
    private String convertMilliToRelative(Long now) {
        String time = DateUtils.getRelativeDateTimeString(this, now , DateUtils.SECOND_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        return time;
    }


    // ArrayList to store match results
    private ArrayList<MatchesObject> resultsMatches = new ArrayList<>();

    // Method to return the list of matches
    private List<MatchesObject> getDataSetMatches() {

        return resultsMatches;
    }

    // Back method to handle back button press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }
}

