package com.zybooks.inventoryapp.abstraction;

import com.zybooks.inventoryapp.model.User;

import java.util.List;

public interface IUsersDbTransactions {
    List<User> getAllUsers() ;
    User getUserByUserName(String username);
    boolean createUser(String username, String password) ;
    boolean isUsernameUsed(String username) ;
}
