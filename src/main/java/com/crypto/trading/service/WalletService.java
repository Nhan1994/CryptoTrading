package com.crypto.trading.service;

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

    public void executeBuy(String userName, String symbol, BigDecimal quantity, BigDecimal totalPrice){
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isEmpty()){
            throw new RuntimeException("No user found for transaction!");
        }
        Optional<Wallet> optionalUsdtWallet =  walletRepository.findByUserAndCurrency(user.get(), "USDT");
        if (optionalUsdtWallet.isEmpty()){
            throw new RuntimeException("No wallet found for transaction!");
        }
        if (optionalUsdtWallet.get().getBalance().compareTo(new BigDecimal(0)) == 0 ){
            throw new RuntimeException("The balance USDT is 0, please add more funds!");
        }
        if (optionalUsdtWallet.get().getBalance().compareTo(totalPrice) < 0){
            throw new RuntimeException("There is not enough balance to buy!");
        }
        Wallet usdtWallet = optionalUsdtWallet.get();
        if (symbol.equals("BTCUSDT")) {
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalPrice));
            walletRepository.save(usdtWallet);

            Optional<Wallet> optionalBtcWallet =  walletRepository.findByUserAndCurrency(user.get(), "BTC");
            if (optionalBtcWallet.isPresent()){
                Wallet btcWallet = optionalBtcWallet.get();
                btcWallet.setBalance(btcWallet.getBalance().add(quantity));
                walletRepository.save(btcWallet);
            } else {
                Wallet newBtcWallet = new Wallet();
                newBtcWallet.setUser(user.get());
                newBtcWallet.setBalance(quantity);
                newBtcWallet.setCurrency("BTC");
                walletRepository.save(newBtcWallet);
            }
        } else if (symbol.equals("ETHUSDT")) {
            usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalPrice));
            walletRepository.save(usdtWallet);

            Optional<Wallet> optionalEthWallet =  walletRepository.findByUserAndCurrency(user.get(), "ETH");
            if (optionalEthWallet.isPresent()){
                Wallet ethWallet = optionalEthWallet.get();
                ethWallet.setBalance(ethWallet.getBalance().add(quantity));
                walletRepository.save(ethWallet);
            } else {
                Wallet newEthWallet = new Wallet();
                newEthWallet.setUser(user.get());
                newEthWallet.setBalance(quantity);
                newEthWallet.setCurrency("ETH");
                walletRepository.save(newEthWallet);
            }
        }
    }

    public void executeSell(String userName, String symbol, BigDecimal quantity, BigDecimal totalPrice){
        Optional<User> user = userRepository.findByUserName(userName);
        if (!user.isPresent()){
            throw new RuntimeException("No user found for transaction!");
        }

        if (symbol.equals("BTCUSDT")) {
            Optional<Wallet> optionalBtcWallet =  walletRepository.findByUserAndCurrency(user.get(), "USDT");
            if (!optionalBtcWallet.isPresent()){
                throw new RuntimeException("No BTC wallet found for sell!");
            }
            if (optionalBtcWallet.get().getBalance().compareTo(new BigDecimal(0)) == 0 ){
                throw new RuntimeException("The balance BTC is 0, please add more funds!");
            }
            if (optionalBtcWallet.get().getBalance().compareTo(quantity) < 0){
                throw new RuntimeException("There is not enough BTC quantity to sell!");
            }

            Wallet btcWallet = optionalBtcWallet.get();
            btcWallet.setBalance(btcWallet.getBalance().subtract(quantity));
            walletRepository.save(btcWallet);

            Optional<Wallet> optionalUsdtWallet =  walletRepository.findByUserAndCurrency(user.get(), "USDT");
            if (optionalUsdtWallet.isPresent()){
                Wallet usdtWallet = optionalUsdtWallet.get();
                usdtWallet.setBalance(usdtWallet.getBalance().add(totalPrice));
            } else {
                Wallet newUsdtWallet = new Wallet();
                newUsdtWallet.setUser(user.get());
                newUsdtWallet.setBalance(totalPrice);
                newUsdtWallet.setCurrency("USDT");
                walletRepository.save(newUsdtWallet);
            }
        } else if (symbol.equals("ETHUSDT")) {
            Optional<Wallet> optionalEthWallet =  walletRepository.findByUserAndCurrency(user.get(), "ETH");
            if (!optionalEthWallet.isPresent()){
                throw new RuntimeException("No ETH wallet found for sell!");
            }
            if (optionalEthWallet.get().getBalance().compareTo(new BigDecimal(0)) == 0 ){
                throw new RuntimeException("The balance ETH is 0, please add more funds!");
            }
            if (optionalEthWallet.get().getBalance().compareTo(quantity) < 0){
                throw new RuntimeException("There is not enough BTC quantity to sell!");
            }

            Wallet ethWallet = optionalEthWallet.get();
            ethWallet.setBalance(ethWallet.getBalance().subtract(quantity));
            walletRepository.save(ethWallet);

            Optional<Wallet> optionalUsdtWallet =  walletRepository.findByUserAndCurrency(user.get(), "USDT");
            if (optionalUsdtWallet.isPresent()){
                Wallet usdtWallet = optionalUsdtWallet.get();
                usdtWallet.setBalance(usdtWallet.getBalance().add(totalPrice));
            } else {
                Wallet newUsdtWallet = new Wallet();
                newUsdtWallet.setUser(user.get());
                newUsdtWallet.setBalance(totalPrice);
                newUsdtWallet.setCurrency("USDT");
                walletRepository.save(newUsdtWallet);
            }
        }
    }
}
