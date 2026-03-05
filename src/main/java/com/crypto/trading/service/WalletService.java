package com.crypto.trading.service;

import com.crypto.trading.dto.DigitalCurrency;
import com.crypto.trading.dto.TradingPair;
import com.crypto.trading.dto.WalletResponse;
import com.crypto.trading.entity.User;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.UserRepository;
import com.crypto.trading.repository.WalletRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        return userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<WalletResponse> getWalletBalances(String userName, String currency) {
        List<WalletResponse> walletResponses = new ArrayList<>();
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isEmpty()){
            throw new RuntimeException("No user found for this action!");
        }
        if (StringUtils.isEmpty(currency)){
            List<Wallet> wallets =  walletRepository.findByUser(user.get());
            wallets.forEach(wallet -> {
                WalletResponse walletResponse = new WalletResponse();
                walletResponse.setBalance(wallet.getBalance());
                walletResponse.setCurrency(wallet.getCurrency());
                walletResponse.setUserName(wallet.getUser().getUserName());
                walletResponses.add(walletResponse);
            });
        } else {
            Optional<Wallet> wallet = walletRepository.findByUserAndCurrency(user.get(), currency);
            if (wallet.isPresent()){
                WalletResponse walletResponse = new WalletResponse();
                walletResponse.setBalance(wallet.get().getBalance());
                walletResponse.setCurrency(wallet.get().getCurrency());
                walletResponse.setUserName(wallet.get().getUser().getUserName());
                walletResponses.add(walletResponse);
            }
        }
        return walletResponses;
    }

    public void executeBuy(User user, String symbol, BigDecimal quantity, BigDecimal totalPrice){
        Optional<Wallet> optionalUSDTWallet =  walletRepository.findByUserAndCurrency(user, DigitalCurrency.USDT.getValue());
        validateUSDTWalletOnBuyOrder(totalPrice, optionalUSDTWallet);
        Wallet usdtWallet = optionalUSDTWallet.get();
        if (symbol.equals(TradingPair.BTCUSDT.getValue())) {
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalPrice));
            walletRepository.save(usdtWallet);

            Optional<Wallet> optionalBtcWallet =  walletRepository.findByUserAndCurrency(user, DigitalCurrency.BTC.getValue());
            if (optionalBtcWallet.isPresent()){
                Wallet btcWallet = optionalBtcWallet.get();
                btcWallet.setBalance(btcWallet.getBalance().add(quantity));
                walletRepository.save(btcWallet);
            } else {
                Wallet newBtcWallet = new Wallet();
                newBtcWallet.setUser(user);
                newBtcWallet.setBalance(quantity);
                newBtcWallet.setCurrency(DigitalCurrency.BTC.getValue());
                walletRepository.save(newBtcWallet);
            }
        } else if (symbol.equals(TradingPair.ETHUSDT.getValue())) {
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalPrice));
            walletRepository.save(usdtWallet);

            Optional<Wallet> optionalEthWallet =  walletRepository.findByUserAndCurrency(user, DigitalCurrency.ETH.getValue());
            if (optionalEthWallet.isPresent()){
                Wallet ethWallet = optionalEthWallet.get();
                ethWallet.setBalance(ethWallet.getBalance().add(quantity));
                walletRepository.save(ethWallet);
            } else {
                Wallet newEthWallet = new Wallet();
                newEthWallet.setUser(user);
                newEthWallet.setBalance(quantity);
                newEthWallet.setCurrency(DigitalCurrency.ETH.getValue());
                walletRepository.save(newEthWallet);
            }
        }
    }

    private void validateUSDTWalletOnBuyOrder(BigDecimal totalPrice, Optional<Wallet> optionalUSDTWallet) {
        if (optionalUSDTWallet.isEmpty()){
            throw new RuntimeException("No wallet found for transaction!");
        }
        if (optionalUSDTWallet.get().getBalance().compareTo(new BigDecimal(0)) == 0 ){
            throw new RuntimeException("The balance USDT is 0, please add more funds!");
        }
        if (optionalUSDTWallet.get().getBalance().compareTo(totalPrice) < 0){
            throw new RuntimeException("There is not enough balance to buy!");
        }
    }

    public void executeSell(User user, String symbol, BigDecimal quantity, BigDecimal totalPrice){
        if (symbol.equals(TradingPair.BTCUSDT.getValue())) {
            Optional<Wallet> optionalBtcWallet =  walletRepository.findByUserAndCurrency(user, DigitalCurrency.BTC.getValue());
            validateBTCWalletOnSellOrder(quantity, optionalBtcWallet);

            Wallet btcWallet = optionalBtcWallet.get();
            btcWallet.setBalance(btcWallet.getBalance().subtract(quantity));
            walletRepository.save(btcWallet);

            Optional<Wallet> optionalUsdtWallet =  walletRepository.findByUserAndCurrency(user, DigitalCurrency.USDT.getValue());
            if (optionalUsdtWallet.isPresent()){
                Wallet usdtWallet = optionalUsdtWallet.get();
                usdtWallet.setBalance(usdtWallet.getBalance().add(totalPrice));
            } else {
                Wallet newUsdtWallet = new Wallet();
                newUsdtWallet.setUser(user);
                newUsdtWallet.setBalance(totalPrice);
                newUsdtWallet.setCurrency(DigitalCurrency.USDT.getValue());
                walletRepository.save(newUsdtWallet);
            }
        } else if (symbol.equals(TradingPair.ETHUSDT.getValue())) {
            Optional<Wallet> optionalETHWallet =  walletRepository.findByUserAndCurrency(user, DigitalCurrency.ETH.getValue());
            validateETHWalletOnSellOrder(quantity, optionalETHWallet);

            Wallet ethWallet = optionalETHWallet.get();
            ethWallet.setBalance(ethWallet.getBalance().subtract(quantity));
            walletRepository.save(ethWallet);

            Optional<Wallet> optionalUSDTWallet = walletRepository.findByUserAndCurrency(user, DigitalCurrency.USDT.getValue());
            if (optionalUSDTWallet.isPresent()){
                Wallet usdtWallet = optionalUSDTWallet.get();
                usdtWallet.setBalance(usdtWallet.getBalance().add(totalPrice));
            } else {
                Wallet newUsdtWallet = new Wallet();
                newUsdtWallet.setUser(user);
                newUsdtWallet.setBalance(totalPrice);
                newUsdtWallet.setCurrency(DigitalCurrency.USDT.getValue());
                walletRepository.save(newUsdtWallet);
            }
        }
    }

    private void validateETHWalletOnSellOrder(BigDecimal quantity, Optional<Wallet> optionalETHWallet) {
        if (optionalETHWallet.isEmpty()){
            throw new RuntimeException("No ETH wallet found for sell!");
        }
        if (optionalETHWallet.get().getBalance().compareTo(new BigDecimal(0)) == 0 ){
            throw new RuntimeException("The balance ETH is 0, please add more funds!");
        }
        if (optionalETHWallet.get().getBalance().compareTo(quantity) < 0){
            throw new RuntimeException("There is not enough BTC quantity to sell!");
        }
    }

    private void validateBTCWalletOnSellOrder(BigDecimal quantity, Optional<Wallet> optionalBtcWallet) {
        if (optionalBtcWallet.isEmpty()){
            throw new RuntimeException("No BTC wallet found for sell!");
        }
        if (optionalBtcWallet.get().getBalance().compareTo(new BigDecimal(0)) == 0 ){
            throw new RuntimeException("The balance BTC is 0, please add more funds!");
        }
        if (optionalBtcWallet.get().getBalance().compareTo(quantity) < 0){
            throw new RuntimeException("There is not enough BTC quantity to sell!");
        }
    }
}
