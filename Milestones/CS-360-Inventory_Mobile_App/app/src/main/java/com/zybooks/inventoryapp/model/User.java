package com.zybooks.inventoryapp.model;

public class User {
    private int id =-1;
    private String username ="" ;
    private String password = "";
    private long createdAt;
    public User(int id, String username, String password){
        this.id= id;
        this.username= username;
        this.password= password;
        this.createdAt = System.currentTimeMillis();
    }
    public User(){
    }

    public User(String username, String password){
        this.username= username;
        this.password= password;
    }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public long getCreatedAt() { return createdAt; }

}
