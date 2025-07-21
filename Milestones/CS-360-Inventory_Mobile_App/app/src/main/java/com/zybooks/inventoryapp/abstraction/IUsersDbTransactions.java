package com.zybooks.inventoryapp.abstraction;

import com.zybooks.inventoryapp.model.UserDto;

import java.util.List;

public interface IUsersDbTransactions {
    List<UserDto> getAllUsers() ;
    UserDto getUserByUserName(String username);
    boolean createUser(String username, String password) ;
    boolean isUsernameUsed(String username) ;
}
