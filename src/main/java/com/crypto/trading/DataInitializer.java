package com.crypto.trading;

import com.crypto.trading.entity.User;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.UserRepository;
import com.crypto.trading.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) {

        if (userRepository.count() > 0) {
            return;
        }

        User user = User.builder()
                .userName("trader1")
                .email("trader1@email.com")
                .build();

        userRepository.save(user);

        walletRepository.save(
                Wallet.builder()
                        .user(user)
                        .currency("USDT")
                        .balance(new BigDecimal("50000.00000000"))
                        .build()
        );

        log.info("Initialize data for user {} with balance {} USDT", "trade1", 50000.0000000);
    }
}
