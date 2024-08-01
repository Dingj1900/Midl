
package com.techelevator.Midl.dao;

import com.techelevator.Midl.model.RegisterUserDto;
import com.techelevator.Midl.model.User;

import java.util.List;

public interface UserDao {

    List<User> getUsers();

    User getUserById(int id);

    User getUserByUsername(String username);

    User createUser(RegisterUserDto user);
}


