package com.example.amie.SwipeCards;

public class cards {
    private String userId;
    private String name;
    private String profileImg;
    private String interest;
    private String university;
    private String location;


    // Constructor that initializes the "cards" object with the given parameters.
    public cards(String userId, String name, String profileImg, String interest, String university, String location) {
        this.userId = userId;
        this.name = name;
        this.profileImg = profileImg;
        this.interest = interest;
        this.university = university;
        this.location = location;
    }

    // Default constructor for creating an empty "cards" object.
    public cards(){

    }

    // Getter method for the user's ID.
    public String getUserId() {
        return userId;
    }

    // Setter method for the user's ID.
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter method for the user's name.
    public String getName() {
        return name;
    }

    // Setter method for the user's name.
    public void setName(String name) {
        this.name = name;
    }

    // Getter method for the user's profile image URL.
    public String getProfileImg() {
        return profileImg;
    }

    // Setter method for the user's profile image URL.
    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    // Getter method for the user's interest.
    public String getInterest() {
        return interest;
    }

    // Setter method for the user's interest.
    public void setInterest(String interest) {
        this.interest = interest;
    }

    // Getter method for the user's university.
    public String getUniversity() {
        return university;
    }

    // Override the "toString" method to provide a custom string representation of the "cards" object.
    @Override
    public String toString() {
        return "cards{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", interest='" + interest + '\'' +
                ", university='" + university + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    // Setter method for the user's university.
    public void setUniversity(String university) {
        this.university = university;
    }

    // Getter method for the user's location.
    public String getLocation() {
        return location;
    }

    // Setter method for the user's location.
    public void setlocation(String location) {
        this.location = location;
    }
}
