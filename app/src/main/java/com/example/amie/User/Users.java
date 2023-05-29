package com.example.amie.User;

import java.io.Serializable;

public class Users implements Serializable {
    private String uid, name, phone, notificationKey;
    private Boolean selected = false;

    public Users(String uid) {
        this.uid = uid;
    }

    // Constructor with name, phone, and notificationKey as parameters
    public Users(String name, String phone, String notificationKey) {
        this.name = name;
        this.phone = phone;
        this.notificationKey = notificationKey;
    }

    // Getter method for name
    public String getName() {
        return name;
    }

    // Setter method for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter method for phone
    public String getPhone() {
        return phone;
    }

    // Setter method for phone
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter method for notificationKey
    public String getNotificationKey() {
        return notificationKey;
    }

    // Setter method for notificationKey
    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }
}
