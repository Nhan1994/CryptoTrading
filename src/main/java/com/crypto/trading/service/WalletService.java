package com.crypto.trading.service;

import com.crypto.trading.dto.DigitalCurrency;
import com.crypto.trading.dto.WalletResponse;
import com.crypto.trading.entity.User;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;

    public List<WalletResponse> getWalletBalances(String userName, String currency) {
        User user = userService.getUserByUserName(userName);
        if (!StringUtils.hasText(currency)){
            return walletRepository.findByUser(user)
                    .stream()
                    .map(this::toWalletResponse)
                    .toList();
        }
        return walletRepository.findByUserAndCurrency(user, currency)
                .map(wallet -> List.of(toWalletResponse(wallet)))
                .orElse(List.of());
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        WalletResponse response = new WalletResponse();
        response.setUserName(wallet.getUser().getUserName());
        response.setCurrency(wallet.getCurrency());
        response.setBalance(wallet.getBalance());
        return response;
    }

    @Transactional
    public void executeBuy(User user, String symbol, BigDecimal quantity, BigDecimal totalPrice){
        Wallet usdtWallet = getWalletOrThrow(user, DigitalCurrency.USDT.getValue());
        validateSufficientBalance(usdtWallet, totalPrice);
        // Deduct total price of buying currency from current USDT balance
        usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalPrice));

        String baseCurrency = extractBaseCurrency(symbol);

        Wallet baseWallet = walletRepository
                .findByUserAndCurrency(user, baseCurrency)
                .orElseGet(() -> createWallet(user, baseCurrency));

        baseWallet.setBalance(baseWallet.getBalance().add(quantity));

        walletRepository.saveAll(List.of(usdtWallet, baseWallet));
    }

    private Wallet getWalletOrThrow(User user, String currency) {
        return walletRepository.findByUserAndCurrency(user, currency)
                .orElseThrow(() -> new TradingBusinessException(
                        currency + " wallet not found", HttpStatus.BAD_REQUEST));
    }

    private void validateSufficientBalance(Wallet wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) <= 0) {
            throw new TradingBusinessException(
                    "Insufficient balance for currency: " + wallet.getCurrency(), HttpStatus.BAD_REQUEST);
        }
    }

    // Get BTC or ETH from trading pair
    private String extractBaseCurrency(String symbol) {
        return symbol.replace(DigitalCurrency.USDT.getValue(), "");
    }

    private Wallet createWallet(User user, String currency) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    @Transactional
    public void executeSell(User user, String symbol, BigDecimal quantity, BigDecimal totalPrice){
        String baseCurrency = extractBaseCurrency(symbol);
        Wallet baseWallet = getWalletOrThrow(user, baseCurrency);
        validateSufficientBalance(baseWallet, quantity);
        baseWallet.setBalance(baseWallet.getBalance().subtract(quantity));

        // Add total price of sold currency into current USDT balance
        Wallet usdtWallet = walletRepository
                .findByUserAndCurrency(user, DigitalCurrency.USDT.getValue())
                .orElseGet(() -> createWallet(user, DigitalCurrency.USDT.getValue()));
        usdtWallet.setBalance(usdtWallet.getBalance().add(totalPrice));
        walletRepository.saveAll(List.of(usdtWallet, baseWallet));
    }
}
