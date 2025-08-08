package com.zybooks.inventoryapp.model;

//TODO: Add Comments.
public class User {
    private String id = "";
    private String emailAddress = "";
    private String password = "";
    private long createdAt;

    public User(String id, String emailAddress, String password) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.password = password;
        this.createdAt = System.currentTimeMillis();
    }

    public User() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
