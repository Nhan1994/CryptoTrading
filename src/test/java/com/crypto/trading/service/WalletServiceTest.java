package com.crypto.trading.service;

import com.crypto.trading.dto.WalletResponse;
import com.crypto.trading.entity.User;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    public static final String BTCUSDT = "BTCUSDT";
    public static final String USDT = "USDT";
    public static final String BTC = "BTC";

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private WalletService walletService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
    }

    @Test
    void testReturnAllWalletBalancesWhenCurrencyNull() {
        Wallet usdt = new Wallet();
        usdt.setUser(user);
        usdt.setCurrency(USDT);
        usdt.setBalance(new BigDecimal("50000"));

        Wallet btc = new Wallet();
        btc.setUser(user);
        btc.setCurrency(BTC);
        btc.setBalance(new BigDecimal("1"));

        when(userService.getUserByUserName("testuser")).thenReturn(user);
        when(walletRepository.findByUser(user)).thenReturn(List.of(usdt, btc));

        List<WalletResponse> result =
                walletService.getWalletBalances("testuser", null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCurrency()).isEqualTo(USDT);

        verify(walletRepository).findByUser(user);
    }

    @Test
    void testReturnWalletByCurrency() {
        Wallet usdt = new Wallet();
        usdt.setUser(user);
        usdt.setCurrency(USDT);
        usdt.setBalance(new BigDecimal("50000"));

        when(userService.getUserByUserName("testuser")).thenReturn(user);
        when(walletRepository.findByUserAndCurrency(user, USDT))
                .thenReturn(Optional.of(usdt));

        List<WalletResponse> result =
                walletService.getWalletBalances("testuser", USDT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo(USDT);
    }

    @Test
    void testExecuteBuySuccessfully() {
        Wallet usdtWallet = new Wallet();
        usdtWallet.setUser(user);
        usdtWallet.setCurrency(USDT);
        usdtWallet.setBalance(new BigDecimal("50000"));

        Wallet btcWallet = new Wallet();
        btcWallet.setUser(user);
        btcWallet.setCurrency(BTC);
        btcWallet.setBalance(new BigDecimal("1"));

        when(walletRepository.findByUserAndCurrency(user, USDT))
                .thenReturn(Optional.of(usdtWallet));

        when(walletRepository.findByUserAndCurrency(user, BTC))
                .thenReturn(Optional.of(btcWallet));

        walletService.executeBuy(
                user,
                BTCUSDT,
                new BigDecimal("1"),
                new BigDecimal("20000")
        );

        assertThat(usdtWallet.getBalance())
                .isEqualByComparingTo("30000");

        assertThat(btcWallet.getBalance())
                .isEqualByComparingTo("2");

        verify(walletRepository).saveAll(any());
    }

    @Test
    void testThrowExceptionWhenInsufficientUsdtBalance() {
        Wallet usdtWallet = new Wallet();
        usdtWallet.setUser(user);
        usdtWallet.setCurrency(USDT);
        usdtWallet.setBalance(new BigDecimal("100"));

        when(walletRepository.findByUserAndCurrency(user, USDT))
                .thenReturn(Optional.of(usdtWallet));

        assertThatThrownBy(() ->
                walletService.executeBuy(
                        user,
                        BTCUSDT,
                        new BigDecimal("1"),
                        new BigDecimal("20000")
                ))
                .isInstanceOf(TradingBusinessException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    void testExecuteSellSuccessfully() {
        Wallet btcWallet = new Wallet();
        btcWallet.setUser(user);
        btcWallet.setCurrency(BTC);
        btcWallet.setBalance(new BigDecimal("2"));

        Wallet usdtWallet = new Wallet();
        usdtWallet.setUser(user);
        usdtWallet.setCurrency(USDT);
        usdtWallet.setBalance(new BigDecimal("10000"));

        when(walletRepository.findByUserAndCurrency(user, BTC))
                .thenReturn(Optional.of(btcWallet));

        when(walletRepository.findByUserAndCurrency(user, USDT))
                .thenReturn(Optional.of(usdtWallet));

        walletService.executeSell(
                user,
                BTCUSDT,
                new BigDecimal("1"),
                new BigDecimal("20000")
        );

        assertThat(btcWallet.getBalance())
                .isEqualByComparingTo("1");

        assertThat(usdtWallet.getBalance())
                .isEqualByComparingTo("30000");

        verify(walletRepository).saveAll(any());
    }

    @Test
    void testThrowExceptionWhenSellingWithoutEnoughCrypto() {
        Wallet btcWallet = new Wallet();
        btcWallet.setUser(user);
        btcWallet.setCurrency(BTC);
        btcWallet.setBalance(new BigDecimal("0.5"));

        when(walletRepository.findByUserAndCurrency(user, BTC))
                .thenReturn(Optional.of(btcWallet));

        assertThatThrownBy(() ->
                walletService.executeSell(
                        user,
                        BTCUSDT,
                        new BigDecimal("1"),
                        new BigDecimal("20000")
                ))
                .isInstanceOf(TradingBusinessException.class);
    }
}
