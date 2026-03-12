package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.entity.User;
import com.csis231.springpostgrescrud.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;

    @Override
    public boolean authenticate(String username, String password) {
        List<User> users = userRepository.findAll();
        for(User user: users){
            if(user.getUsername().equals(username)&&
            user.getPassword().equals(password)){
                return true;
            }
        }
        return false;

    }
}
