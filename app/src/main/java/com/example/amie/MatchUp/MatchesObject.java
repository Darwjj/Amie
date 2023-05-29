package com.example.amie.MatchUp;

import com.example.amie.User.Users;

import java.util.ArrayList;

public class MatchesObject {
    private String userId, name, profileImg, interest, university, location, lastMessage, lastTimeStamp, lastSeen, childId;
    private ArrayList<Users> userObjectArrayList = new ArrayList<>();

    public MatchesObject(String userId, String name, String profileImg, String interest, String university, String location, String lastMessage, String lastTimeStamp, String lastSeen, String childId) {
        this.userId = userId;
        this.name = name;
        this.profileImg = profileImg;
        this.interest = interest;
        this.university = university;
        this.location = location;
        this.lastMessage = lastMessage;
        this.lastTimeStamp = lastTimeStamp;
        this.lastSeen = lastSeen;
        this.childId = childId;
    }

    // Get userObjectArrayList method
    public ArrayList<Users> getUserObjectArrayList() {
        return userObjectArrayList;
    }

    // Add user to the ArrayList method
    public void addUserToArrayList(Users mUser) {
        userObjectArrayList.add(mUser);
    }

    // Getter and setter methods for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter methods for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter methods for profileImg
    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    // Getter and setter methods for interest
    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    // Getter and setter methods for university
    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    // Getter and setter methods for location
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter and setter methods for lastMessage
    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    // Getter and setter methods for lastTimeStamp
    public String getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(String lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    // Getter and setter methods for lastSeen
    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    // Getter and setter methods for childId
    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}
