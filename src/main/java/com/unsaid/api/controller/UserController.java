package com.unsaid.api.controller;

import com.unsaid.api.entity.User;
import com.unsaid.api.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public User createUser() {
        return userService.createAnonymousUser();
    }
}
