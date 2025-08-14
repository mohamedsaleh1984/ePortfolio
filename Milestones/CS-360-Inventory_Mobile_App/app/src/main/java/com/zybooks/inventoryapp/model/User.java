package com.zybooks.inventoryapp.model;

// A representation to User information
public class User {
    private String id;
    private String emailAddress;
    private String password ;
    private long createdAt;

    public User(String id, String emailAddress, String password) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.password = password;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters
    public String getId() {
        return id;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
