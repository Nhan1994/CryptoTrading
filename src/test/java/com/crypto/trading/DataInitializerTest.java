package com.crypto.trading;

import com.crypto.trading.entity.User;
import com.crypto.trading.repository.UserRepository;
import com.crypto.trading.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void testNotInitializeDataWhenUserAlreadyExists() {
        User savedUser = User.builder()
                .id(1L)
                .userName("trader1")
                .email("trader1@email.com")
                .build();
        when(userRepository.findByUserName("trader1")).thenReturn(Optional.of(savedUser));

        dataInitializer.run();

        verify(userRepository, never()).save(any());
        verify(walletRepository, never()).save(any());
    }

    @Test
    void testInitializeUserAndWalletWhenNoUserExists() {
        when(userRepository.findByUserName("trader1")).thenReturn(Optional.empty());
        User savedUser = User.builder()
                .id(1L)
                .userName("trader1")
                .email("trader1@email.com")
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        dataInitializer.run();

        verify(userRepository).save(argThat(user ->
                user.getUserName().equals("trader1")
                        && user.getEmail().equals("trader1@email.com")
        ));

        verify(walletRepository).save(argThat(wallet ->
                wallet.getCurrency().equals("USDT")
                        && wallet.getBalance().compareTo(new BigDecimal("50000.00000000")) == 0
        ));
    }
}
