package com.example.amie;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amie.SwipeCards.arrayAdapter;
import com.example.amie.SwipeCards.cards;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.ShowCasePosition;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.radius.Radius;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private cards cards_data[];
    private com.example.amie.SwipeCards.arrayAdapter arrayAdapter;
    private int i;
    private  String tag;
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    boolean firstStart;
    private String currentUId, notification, sendMessageText;
    private boolean activityStarted;
    private DatabaseReference usersDb;

    // Set up ListView for displaying cards
    ListView listView;
    List<cards> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityStarted = true;
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar)findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);
        setupTopNavigationView();
        getUsersData();
        // Check if the app is launched for the first time
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        tag = "MainActivity";
        // Initialize Firebase components
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null && mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
        else{
            Log.d(tag, "Authorization failed");
            Toast.makeText(getApplicationContext(), "Auth failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up OneSignal notifications
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                // .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                usersDb.child(currentUId).child("notificationKey").setValue(userId);
            }
        });


        Log.d(tag, "onCreate " + currentUId);

        checkUserSex();

        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems );


        // Set up SwipeFlingAdapterView for swiping functionality
        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        // Set FlingListener to handle swipe events
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d(tag, "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {

                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();

                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();

                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject)  {
                Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Display a tooltip for user profile with a given ShowCasePosition.
    private void showToolTip_profile(ShowCasePosition position) {
        new ShowCaseView.Builder(MainActivity.this)
                .withTypedPosition(position)
                .withTypedRadius(new Radius(186F))
                .withContent("First time upload your profile picture and click on Confirm other wise your app will not work")
                .build()
                .show(MainActivity.this);
    }
    // Display a tooltip for matches with a given ShowCasePosition.
    private void showToolTip_matches(ShowCasePosition position) {
        new ShowCaseView.Builder(MainActivity.this)
                .withTypedPosition(position)
                .withTypedRadius(new Radius(186F))
                .withContent("Find you matches and begin chatting!")
                .build()
                .show(MainActivity.this);
    }

    // Handle the Dislike button click.
    public void DislikeBtn(View v) {
        if (rowItems.size() != 0) {
            cards card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();

            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();

            //Display a banner when no cards are available to display
            TextView tv = (TextView)findViewById(R.id.noCardsBanner);
            if(rowItems.size() == 0) {
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }

            Intent btnClick = new Intent(MainActivity.this, DislikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileImg());
            startActivity(btnClick);

        }
    }

    // Handle the Like button click.
    public void LikeBtn(View v) {
        if (rowItems.size() != 0) {
            cards card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            //check matches
            usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
            isConnectionMatch(userId);
            Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();

            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();


            //Display a banner when no cards are available to display
            TextView tv = (TextView)findViewById(R.id.noCardsBanner);
            if(rowItems.size() == 0) {
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }

            Intent btnClick = new Intent(MainActivity.this, LikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileImg());
            startActivity(btnClick);

        }
    }

    // Check if there's a match between the current user and the passed userId.
    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        usersDb.child(currentUId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    sendMessageText = dataSnapshot.getValue().toString();
                else
                    sendMessageText = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(!currentUId.equals(userId)) {
            currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(MainActivity.this, "" +
                                "New Connection", Toast.LENGTH_LONG).show();

                        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                        Map mapLastTimeStamp = new HashMap<>();
                        long now  = System.currentTimeMillis();
                        String timeStamp = Long.toString(now);
                        mapLastTimeStamp.put("lastTimeStamp", timeStamp);

                        usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                        usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).updateChildren(mapLastTimeStamp);

                        usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                        usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).updateChildren(mapLastTimeStamp);

                        notification = " ";

                        DatabaseReference notificationID = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("notificationKey");
                        notificationID.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    notification = snapshot.getValue().toString();
                                    Log.d("sendChat", notification);
                                    new NotificationActivity("You have a new match!", "", notification, null, null );
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }
    }

    private String userInterest, userUniversity;
    private String oppositeUserInterest, oppositeUserUniversity;

    // Method to check user's sex, and set user's and opposite user's interest and university
    public void checkUserSex(){

        // Get current FirebaseUser and DatabaseReference for user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());

        // Attach listener to retrieve user's data once
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("CardSearch", dataSnapshot.toString());

                // Check if "Interest" child exists
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("interest").getValue() != null){
                        // Log.d("CardSearch", "exists coloumn called");

                        // Retrieve user's interest and university values
                        userInterest = dataSnapshot.child("interest").getValue().toString();
                        userUniversity = dataSnapshot.child("university").getValue().toString();
                        //  Log.d("CardSearch", "datachange called");

                        // Set opposite user's interest and university values
                        oppositeUserUniversity = userInterest;
                        oppositeUserInterest = userUniversity;

                        // Get opposite sex users based on the retrieved data
                        getOppositeSexUsers(oppositeUserUniversity, oppositeUserInterest);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Method to get opposite sex users based on the given parameters
    public void getOppositeSexUsers(final String oppositeUserUniversity, final String oppositeUserInterest){

        // Attach ChildEventListener to usersDb
        usersDb.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // Check if dataSnapshot exists, and dataSnapshot's key is not equal to current user's ID
                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(currentUId)) {
                    //Log.d("CardSearch", "getOppositeSex called");
                    // Check for various conditions to display user cards


                    // If the dataSnapshot's child "give (university)" and "need (interest)" exist and match opposite user's give(university) and need(interest)
                    // Also, check if current user has not been "nope" or "yeps" by dataSnapshot's user
                    if (dataSnapshot.child("university").exists() && dataSnapshot.child("interest").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("university").getValue().toString().equals(oppositeUserUniversity) && dataSnapshot.child("interest").getValue().toString().equals(oppositeUserInterest)) {
                        String profileImg = "default";
                        if (!dataSnapshot.child("profileImg").getValue().equals("default")) {
                            profileImg = dataSnapshot.child("profileImg").getValue().toString();
                        }
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImg, dataSnapshot.child("interest").getValue().toString(), dataSnapshot.child("university").getValue().toString(), dataSnapshot.child("location").getValue().toString());
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    // If the dataSnapshot's child "give (university)" exists and matches opposite user's give (university)
                    else if( dataSnapshot.child("university").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("university").getValue().toString().equals(oppositeUserUniversity)){
                        String profileImg = "default";
                        if (!dataSnapshot.child("profileImg").getValue().equals("default")) {
                            profileImg = dataSnapshot.child("profileImg").getValue().toString();
                        }
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImg, dataSnapshot.child("interest").getValue().toString(), dataSnapshot.child("university").getValue().toString(),  dataSnapshot.child("location").getValue().toString());
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    // If the dataSnapshot's child "need (interest)" exists and matches opposite user's need (interest)
                    else if( dataSnapshot.child("interest").exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId) && !dataSnapshot.child("connections").child("yeps").hasChild(currentUId) && dataSnapshot.child("interest").getValue().toString().equals(oppositeUserInterest)){
                        String profileImg = "default";
                        if (!dataSnapshot.child("profileImg").getValue().equals("default")) {
                            profileImg = dataSnapshot.child("profileImg").getValue().toString();
                        }
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImg, dataSnapshot.child("interest").getValue().toString(), dataSnapshot.child("university").getValue().toString(),  dataSnapshot.child("location").getValue().toString());
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
                //spinner.setVisibility(View.GONE);


                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.noCardsBanner);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }
            // Other required ChildEventListener methods
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    /**
     * setup top tool bar
     */
    // Method to set up top navigation view
    private void setupTopNavigationView() {
        Log.d("", "setupTopNavigationView: setting up TopNavigationView");
        BottomNavigationViewEx tvEx = findViewById(R.id.topNavViewBar);
        NavigationActivity.setupTopNavigationView(tvEx);
        NavigationActivity.enableNavigation(MainActivity.this, tvEx);
        Menu menu = tvEx.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        //show tool tip
        View profile_view = findViewById(R.id.ic_profile);
        View matches_view = findViewById(R.id.ic_matched);

        if (firstStart) {
            showToolTip_profile(new ViewPosition(profile_view));
            //showToolTip_matches(new ViewPosition(matches_view));
        }
        SharedPreferences newPref = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = newPref.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();

    }

    // Override Back method
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }
    // NotificationOpenedHandler class to handle opening of notifications
    public  class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        private Context mContext;
        public NotificationOpenedHandler(Context context) {
            mContext = context;
        }
        @Override
        public void notificationOpened(OSNotificationOpenResult result)
        {
            // Get actionType and additional data from result
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String activityToBeOpened;
            String activity;

            if (data != null)
            {
                // Get activityToBeOpened from data, and open the specified activity if it exists
                activityToBeOpened = data.optString("activityToBeOpened", null);
                if (activityToBeOpened != null && activityToBeOpened.equals("MatchesActivity"))
                {
                    try {
                        Intent intent3 = new Intent(getApplicationContext(), SettingsActivity.class);
                        intent3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);

                        Log.i("OneSignal", "customkey set with value: " + activityToBeOpened);
                        mContext.startActivity(intent3);
                    }
                    catch (Throwable t){
                        t.printStackTrace();
                    }
                }
            }
        }
    }
    // Method to get users data and populate the UI with user cards
    void getUsersData()
    {
      rowItems = new ArrayList<cards>();
      FirebaseDatabase database = FirebaseDatabase.getInstance();
      DatabaseReference ref = database.getReference("Users");

      ref.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
          for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
            cards userCard = userSnapshot.getValue(cards.class);
            rowItems.add(userCard);
            arrayAdapter.notifyDataSetChanged();
          }
          // populate your UI with the user profile data in userList
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
          Log.e(TAG, "onCancelled", databaseError.toException());
        }
      });
    }

}

