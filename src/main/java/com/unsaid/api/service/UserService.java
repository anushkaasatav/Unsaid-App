package com.unsaid.api.service;

import com.unsaid.api.entity.User;
import com.unsaid.api.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createAnonymousUser() {
        String handle;
        do {
            handle = HandleGenerator.generate();
        } while (userRepository.existsByHandle(handle));

        User user = new User(handle);
        return userRepository.save(user);
    }
}
