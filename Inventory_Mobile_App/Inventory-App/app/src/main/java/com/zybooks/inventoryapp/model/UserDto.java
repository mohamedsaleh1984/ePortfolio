package com.zybooks.inventoryapp.model;

public class UserDto {
    private int id =-1;
    private String username ="" ;
    private String password = "";

    public UserDto(int id,String username, String passowrd){
        this.id= id;
        this.username= username;
        this.password= passowrd;
    }
    public UserDto(){
    }

    public UserDto(String username, String passowrd){
        this.username= username;
        this.password= passowrd;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}
