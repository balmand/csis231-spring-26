package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.LoginDto;
import com.csis231.springpostgrescrud.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/login")
public class UserController {
    private UserService userService;

    @PostMapping
    public boolean login(@RequestBody LoginDto loginDto){
        return userService.authenticate(
                loginDto.getUsername(),
                loginDto.getPassword()
        );
    }
}
