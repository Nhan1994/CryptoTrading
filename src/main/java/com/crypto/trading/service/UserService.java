package com.crypto.trading.service;

import com.crypto.trading.entity.User;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
                    log.warn("User not found: {}", userName);
                    return new TradingBusinessException("User not found: " + userName, HttpStatus.BAD_REQUEST);
                });
    }

}
