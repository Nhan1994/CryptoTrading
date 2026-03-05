package com.crypto.trading.service;

import com.crypto.trading.entity.User;
import com.crypto.trading.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User getUserByUserName(String userName){
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> {
                    log.warn("No user found for {}", userName);
                    return new RuntimeException("User not found: " + userName);
                });
    }

}
