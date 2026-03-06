package com.crypto.trading.service;

import com.crypto.trading.entity.User;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testReturnUserWhenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setUserName("testtrader1");

        when(userRepository.findByUserName("testtrader1"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByUserName("testtrader1");

        assertThat(result).isNotNull();
        assertThat(result.getUserName()).isEqualTo("testtrader1");

        verify(userRepository).findByUserName("testtrader1");
    }

    @Test
    void testThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUserName("testtrader1"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.getUserByUserName("testtrader1"))
                .isInstanceOf(TradingBusinessException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByUserName("testtrader1");
    }
}
