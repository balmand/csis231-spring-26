package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.UserDto;
import com.csis231.springpostgrescrud.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        String username = authentication == null ? null : authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }
}

