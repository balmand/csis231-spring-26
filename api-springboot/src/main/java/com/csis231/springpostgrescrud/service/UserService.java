package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerUser(UserDto userDto);
    UserDto getUserById(Long id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    boolean authenticateUser(String username, String password);
}
