package com.csis231.springpostgrescrud.service;
import com.csis231.springpostgrescrud.dto.LoginDto;
public interface UserService {
    boolean authenticate(String username, String password);
}
