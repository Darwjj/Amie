package com.example.amie.Messages;

public class MessageObject {
    private String message;
    private Boolean currentUser;
    private Boolean isSeen;

    public MessageObject(String message, Boolean currentUser, Boolean isSeen) {
        this.message = message;
        this.currentUser = currentUser;
        this.isSeen = isSeen;
    }

    // Getter for the message
    public String getMessage() {
        return message;
    }

    // Setter for the message
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter for the current user flag
    public Boolean getCurrentUser() {
        return currentUser;
    }

    // Getter for the current user flag
    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }

    // Getter for the seen flag
    public Boolean getSeen() {
        return isSeen;
    }

    // Setter for the seen flag
    public void setSeen(Boolean seen) {
        isSeen = seen;
    }
}
