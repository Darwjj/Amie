package com.example.amie.Messages;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.amie.MatchUp.MatchesActivity;
import com.example.amie.R;
import com.example.amie.NotificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private EditText mSendEditText;
    private ImageButton mBack;
    private ImageButton mSendButton ;
    private String notification;
    private String currentUserID, matchId, chatId;
    private String matchName, matchUniversity, matchInterest, matchLocation, matchProfile;
    private String lastMessage, lastTimeStamp;
    private String message, createdByUser, isSeen, messageId, currentUserName;
    private Boolean currentUserBoolean;
    ValueEventListener seenListener;
    DatabaseReference mDatabaseUser, mDatabaseChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get match data from Intent extras
        matchId = getIntent().getExtras().getString("matchId");
        matchName = getIntent().getExtras().getString("matchName");
        matchUniversity = getIntent().getExtras().getString("university");
        matchInterest = getIntent().getExtras().getString("interest");
        matchLocation = getIntent().getExtras().getString("location");
        matchProfile = getIntent().getExtras().getString("profile");
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize database references for user and chat
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserID).child("connections").child("matches").child(matchId).child("ChatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        // Get chat ID
        getChatId();

        // Initialize RecyclerView and set its properties
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mChatLayoutManager = new LinearLayoutManager(MessageActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        // Populate chat messages in RecyclerView
        getChatMessages();
        mChatAdapter = new MessageAdapter(getDataSetChat(), MessageActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.message);
        mBack = findViewById(R.id.chatBack);

        mSendButton = findViewById(R.id.send);

        // Set onClickListener for send button to send message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Add onLayoutChangeListener to handle keyboard appearance and smooth scroll
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        // Set onClickListener for back button to navigate back to MatchesActivity
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageActivity.this, MatchesActivity.class);
                startActivity(i);
                finish();
                return;
            }
        });

        // Initialize and set Toolbar
        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);

        // Update onChat value in database for current user
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat = new HashMap();
        onchat.put("onChat", matchId);
        reference.updateChildren(onchat);

        // Update lastSeen value for the match in the database
        DatabaseReference current = FirebaseDatabase.getInstance().getReference("Users")
                .child(matchId).child("connections").child("matches").child(currentUserID);
        Map lastSeen = new HashMap();
        lastSeen.put("lastSeen", "false");
        current.updateChildren(lastSeen);
    }

    @Override
    protected void onPause() {
        // Update onChat value to "None" when ChatActivity is paused
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat = new HashMap();
        onchat.put("onChat", "None");
        reference.updateChildren(onchat);
        super.onPause();
    }

    @Override
    protected void onStop() {
        // Update onChat value to "None" when ChatActivity is stopped
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        Map onchat = new HashMap();
        onchat.put("onChat", "None");
        reference.updateChildren(onchat);
        super.onStop();
    }

    private void seenMessage(final String text) {
        // Update onChat value to "None" when ChatActivity is stopped
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(matchId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())  {
                    if (dataSnapshot.child("onChat").exists()) {
                        if (dataSnapshot.child("notificationKey").exists())
                            notification = dataSnapshot.child("notificationKey").getValue().toString();
                        else
                            notification = "";

                        // If match user is not on chat with the current user, send a notification
                        if (!dataSnapshot.child("onChat").getValue().toString().equals(currentUserID)) {
                            new NotificationActivity(text, "New message from: " + currentUserName, notification,
                                    "activityToBeOpened", "MatchesActivity");
                        }
                        else {
                            // Update lastSend value in the database
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(currentUserID).child("connections").child("matches").child(matchId);
                            Map seenInfo = new HashMap();
                            seenInfo.put("lastSend", "false");
                            reference.updateChildren(seenInfo);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for the chat activity
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
//        TextView mMatchNameTextView = (TextView) findViewById(R.id.chatToolbar);
//        mMatchNameTextView.setText(matchName);
        return true;
    }
    // Show the match's profile when the profile icon is clicked
    public void showProfile(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.item_profile, null);//later we do item_profile

        TextView name = (TextView) popupView.findViewById(R.id.name);
        ImageView image = (ImageView) popupView.findViewById(R.id.image);
        TextView location = (TextView) popupView.findViewById(R.id.budget);
        ImageView mInterestImage = (ImageView) popupView.findViewById(R.id.needImage);
        ImageView mUniversityImage = (ImageView) popupView.findViewById(R.id.giveImage);

        name.setText(matchName);
        location.setText(matchLocation);

        //Interest Image
        if (matchInterest.equals("Art"))
            mInterestImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.art));
        else if (matchInterest.equals("Travel"))
            mInterestImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.travel));
        else if (matchInterest.equals("Books"))
            mInterestImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.books));
        else if (matchInterest.equals("Films"))
            mInterestImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.film));
        else if (matchInterest.equals("Science"))
            mInterestImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.science));
        else
            mInterestImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.none));


        // University Image
        if (matchInterest.equals("De Montfort University"))
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dmu));
        else if (matchInterest.equals("University of Leicester"))
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.uol));
        else if (matchInterest.equals("University of Birmingham"))
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.birmingham));
        else if (matchInterest.equals("Coventry University"))
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.coventry));
        else if (matchInterest.equals("University of Nottingham"))
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.universityofnottingham));
        else if (matchInterest.equals("University of Manchester"))
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.manchester));
        else
            mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.none));

        // Load match's profile image
        switch (matchProfile) {
            case "default" :
                Glide.with(popupView.getContext()).load(R.drawable.suelo).into(image);
                break;
            default:
                Glide.clear(image);
                Glide.with(popupView.getContext()).load(matchProfile).into(image);
                break;
        }

        int width = LinearLayout.LayoutParams.WRAP_CONTENT ;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        hideSoftKeyBoard();

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(v, Gravity.CENTER, 0,0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

    }

    // Method to hide the soft keyboard
    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm.isAcceptingText())   {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.unmatch) {
            // Show confirmation dialog when "Unmatch" is selected from the options menu
            new AlertDialog.Builder(MessageActivity.this)
                    .setTitle("Unmatch")
                    .setMessage("Are you sure you want to unmatch?")
                    .setPositiveButton("Unmatch", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If the user confirms the unmatch, delete the match from the database
                            deleteMatch(matchId);
                            // Return to the MatchesActivity
                            Intent intent = new Intent(MessageActivity.this, MatchesActivity.class);
                            startActivity(intent);
                            finish();
                            // Display a toast message indicating that the unmatch was successful
                            Toast.makeText(MessageActivity.this, "Unmatch successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else if (item.getItemId() == R.id.viewProfile) {
            // When "View Profile" is selected from the options menu, show the user's profile
            showProfile(findViewById(R.id.content));
        }
        // Call the superclass implementation of onOptionsItemSelected to handle other menu items
        return super.onOptionsItemSelected(item);
    }

    // Delete a match from the database
    private void deleteMatch(String matchId) {
        // Get the database references for the match and the users involved in the match
        DatabaseReference matchId_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserID).child("connections").child("matches").child(matchId);
        DatabaseReference userId_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(matchId).child("connections").child("matches").child(currentUserID);
        DatabaseReference yeps_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(matchId).child("connections").child("yeps").child(currentUserID);
        DatabaseReference yeps_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserID).child("connections").child("yeps").child(matchId);

        DatabaseReference matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        // Remove the match and related data from the database
        matchId_chat_dbReference.removeValue();
        matchId_in_UserId_dbReference.removeValue();
        userId_in_matchId_dbReference.removeValue();
        yeps_in_matchId_dbReference.removeValue();
        yeps_in_UserId_dbReference.removeValue();
    }

    // Send a message
    private void sendMessage()   {
        // Get the text entered in the message input field
        final String sendMessageText = mSendEditText.getText().toString();
        long now = System.currentTimeMillis();
        String timeStamp = Long.toString(now);

        if (!sendMessageText.isEmpty()) {
            // Create a new message in the database
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);
            newMessage.put("timeStamp", timeStamp);
            newMessage.put("seen", "false");

            // Get the current user's name from the database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.child("name").exists())
                            currentUserName = dataSnapshot.child("name").getValue().toString();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            lastMessage = sendMessageText;
            lastTimeStamp = timeStamp;
            updateLastMessage();
            seenMessage(sendMessageText);
            newMessageDb.setValue(newMessage);
        }
        mSendEditText.setText(null);
    }

    private void updateLastMessage() {
        // Update the current user's and the match's last message and timestamp in the database
        DatabaseReference currUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID)
                .child("connections").child("matches").child(matchId);
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(matchId)
                .child("connections").child("matches").child(currentUserID);

        Map lastMessageMap = new HashMap();
        lastMessageMap.put("lastMessage", lastMessage);
        Map lastTimestampMap = new HashMap();
        lastTimestampMap.put("lastTimeStamp", lastTimeStamp);

        Map lastSeen = new HashMap();
        lastSeen.put("lastSeen", "true");
        currUserDb.updateChildren(lastSeen);
        currUserDb.updateChildren(lastMessageMap);
        currUserDb.updateChildren(lastTimestampMap);

        matchDb.updateChildren(lastMessageMap);
        matchDb.updateChildren(lastTimestampMap);
    }

    private void getChatId() {
        // Get the chat ID from the database
        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    chatId = dataSnapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getChatMessages() {
        // Get all the messages in the chat and update the chat adapter
        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    messageId = null;
                    message = null;
                    createdByUser = null;
                    isSeen = null;
                    if (dataSnapshot.child("text").getValue() != null ){
                        message = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("createdByUser").getValue() != null ) {
                        createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                    }
                    if (dataSnapshot.child("seen").getValue() != null) {
                        isSeen = dataSnapshot.child("seen").getValue().toString();
                    }
                    else isSeen = "true";

                    messageId = dataSnapshot.getKey().toString();
                    if (message != null && createdByUser != null) {
                        currentUserBoolean = false;
                        if (createdByUser.equals(currentUserID)) {
                            currentUserBoolean = true;
                        }
                        MessageObject newMessage = null;
                        if (isSeen.equals("false")) {
                            if (!currentUserBoolean) {
                                isSeen = "true";
//
//                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat")
//                                        .child(chatId).child(messageId);
//                                Map seenInfo = new HashMap();
//                                seenInfo.put("seen", "true");
//                                reference.updateChildren(seenInfo);

                                newMessage = new MessageObject(message, currentUserBoolean, true);
                            }
                            else {
                                newMessage = new MessageObject(message, currentUserBoolean, false);
                            }
                        }
                        else
                            newMessage = new MessageObject(message, currentUserBoolean, true);
                        DatabaseReference usersInChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(matchId);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();
                        if (mRecyclerView.getAdapter() != null && resultsChat.size() > 0 )
                            mRecyclerView.smoothScrollToPosition(resultsChat.size() - 1);
                        else
                            Toast.makeText(MessageActivity.this, "Chat empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<MessageObject> resultsChat = new ArrayList<>();
    private List<MessageObject> getDataSetChat() {
        return resultsChat;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }
}
