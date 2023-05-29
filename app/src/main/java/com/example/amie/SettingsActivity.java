package com.example.amie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.amie.SwipeCards.cards;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SettingsActivity extends AppCompatActivity {

    private EditText mNameField, mPhoneField;
    private ProgressBar spinner;
    private Button mConfirm;
    private ImageButton mBack;
    private ImageView mProfileImage;
    private EditText mLocation;
    private EditText mSex;
    private Spinner interest, university;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    private String userId, name, phone, profileImg, userSex, userLocation, userInterest, userUniversity;
    private int interestIndex, universityIndex;
    private Uri resultUri;
    private Object MembershipActivity;

    // onCreate method is called when the activity is first created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinner = (ProgressBar) findViewById(R.id.pBar);
        spinner.setVisibility(View.GONE);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mSex = (EditText) findViewById(R.id.sex);

        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        mBack = findViewById(R.id.settingsBack);

        mConfirm = (Button) findViewById(R.id.confirm);
        mLocation = (EditText) findViewById(R.id.budget_settings);
        interest = (Spinner) findViewById(R.id.spinner_need_settings);
        university = (Spinner) findViewById(R.id.spinner_give_setting);


        // Initialize Firebase authentication and user database
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null && mAuth.getCurrentUser() != null)
            userId = mAuth.getCurrentUser().getUid();
        else {
            finish();
        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.services, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        interest.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter_give = ArrayAdapter.createFromResource(this,
                R.array.services, android.R.layout.simple_spinner_item);
        adapter_give.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        university.setAdapter(adapter_give);

        // Retrieve and display user information
        getUserInfo();

        // Set an onClickListener to update profile image when clicked
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    Toast.makeText(SettingsActivity.this, "Please allow access to continue!", Toast.LENGTH_SHORT).show();
                    requestPermission();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
            }
        });

        // Set an onClickListener to confirm changes and navigate to the main activity
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserDatabase.setValue(new cards(userId,name,profileImg,interest.getSelectedItem().toString(),university.getSelectedItem().toString(),userLocation));
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        // Set an onClickListener to navigate back to the main activity without making changes
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.settings_toolbartag);
        setSupportActionBar(toolbar);
    }

    // Create an options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    // Check if the app has permission to access external storage
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    // Request permission to access external storage
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] { READ_EXTERNAL_STORAGE}, 100);
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            } else {
                Toast.makeText(this, "Please allow access to continue!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Override the onOptionsItemSelected method to handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // If the Contact Us menu item is selected, show an AlertDialog with contact information
        if (item.getItemId() == R.id.ContactUs) {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Contact Us")
                    .setMessage("Contact us: santiagoandrangopulupa.com")
                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        // If the Membership menu item is selected, show an AlertDialog and navigate to MembershipActivity on positive button click
        else if (item.getItemId() == R.id.logout) {
            spinner.setVisibility(View.VISIBLE);
            mAuth.signOut();
            Toast.makeText(this, "Log Out successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, LogandRegActivity.class);
            startActivity(intent);
            finish();
            spinner.setVisibility(View.GONE);
        }
        // If the Membership menu item is selected, show an AlertDialog and navigate to MembershipActivity on positive button click
        else if (item.getItemId() == R.id.Membership) {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Do you want contact with people from other universities?")
                    .setMessage("Click on continue, please")
                    .setPositiveButton("Click", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SettingsActivity.this, MembershipActivity.class);
                                        startActivity(intent);
                                        finish();
                                        spinner.setVisibility(View.VISIBLE);
                                        return;
                                }
                    })
                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        // If the deleteAccount menu item is selected, show an AlertDialog and delete the user's account on positive button click
        else if (item.getItemId() == R.id.deleteAccount) {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Are you sure?")
                    .setMessage("Deleting your account will result in completely removing your account from the system")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    spinner.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        deleteUserAccount(userId);
                                        Toast.makeText(SettingsActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SettingsActivity.this, LogandRegActivity.class);
                                        startActivity(intent);
                                        finish();
                                        spinner.setVisibility(View.GONE);
                                        return;
                                    }
                                    else {
                                        Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        Intent intent = new Intent(SettingsActivity.this, LogandRegActivity.class);
                                        startActivity(intent);
                                        finish();
                                        spinner.setVisibility(View.VISIBLE);
                                        return;
                                    }
                                }
                            });
                        }
                    })

                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to delete a match and associated data from Firebase
    public void deleteMatch(String matchId, String chatId) {
        DatabaseReference matchId_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches").child(matchId);
        DatabaseReference userId_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches").child(userId);
        DatabaseReference yeps_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("yeps").child(userId);
        DatabaseReference yeps_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("yeps").child(matchId);

        DatabaseReference matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        matchId_chat_dbReference.removeValue();
        matchId_in_UserId_dbReference.removeValue();
        userId_in_matchId_dbReference.removeValue();
        yeps_in_matchId_dbReference.removeValue();
        yeps_in_UserId_dbReference.removeValue();

    }

    // Method to delete the user account and associated data from Firebase
    private void deleteUserAccount(String userId) {
        DatabaseReference curruser_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        DatabaseReference curruser_matches_ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches");

        curruser_matches_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot match : dataSnapshot.getChildren()) {
                        deleteMatch(match.getKey(), match.child("ChatId").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        curruser_matches_ref.removeValue();
        curruser_ref.removeValue();
    }

    // Method to get the user's information and display it on the screen
    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("name") != null) {
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if (map.get("phone") != null) {
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if (map.get("sex") != null) {
                        userSex = map.get("sex").toString();
                    }
                    if (map.get("location") != null) {
                        userLocation = map.get("location").toString();
                    }
                    else
                        userLocation = "0";
                    if (map.get("university") != null ) {
                        userUniversity = map.get("university").toString();
                    }
                    else
                        userUniversity = "";
                    if (map.get("interest") != null) {
                        userInterest = map.get("interest").toString();
                    }
                    else
                        userInterest = "";

                    String[] services = getResources().getStringArray(R.array.services);
                    interestIndex = universityIndex = 0;
                    for (int i =0; i<services.length; i++){
                        if (userInterest.equals(services[i]))
                            interestIndex = 1;
                        if (userUniversity.equals(services[i]))
                            universityIndex = 1;
                    }

                    interest.setSelection(interestIndex);
                    university.setSelection(universityIndex);
                    mLocation.setText(userLocation);

                    Glide.clear(mProfileImage);
                    if (map.get("profileImg") != null) {
                        profileImg = map.get("profileImg").toString();
                        switch (profileImg) {
                            case "default" :
                                Glide.with(getApplication()).load(R.drawable.profile).into(mProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImg).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Method to save the user's information to Firebase
    private void saveUserInformation()  {
        name = mNameField.getText().toString();
        phone = mPhoneField.getText().toString();
        userLocation = mLocation.getText().toString();
        userUniversity = university.getSelectedItem().toString();
        userInterest = interest.getSelectedItem().toString();

        // Create a HashMap to store the user's information
        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("phone", phone);
        userInfo.put("interest", interest);
        userInfo.put("university", university);
        userInfo.put("location", userLocation);
        // Update the user's information in Firebase
        mUserDatabase.updateChildren(userInfo);
        // If a new profile image is selected, upload it to Firebase Storage
        if (resultUri != null) {
            StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userId);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uri.isComplete());
                    Uri downloadUri = uri.getResult();
                    Map userInfo = new HashMap();
                    userInfo.put("profileImg", downloadUri.toString());
                    mUserDatabase.updateChildren(userInfo);

                    finish();
                    return;
                }
            });
        }
        else {
            finish();
        }
    }

    // Handle the result of the Image Picker activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If the request code is 1 and the result is OK, get the image URI and set the profile image
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
