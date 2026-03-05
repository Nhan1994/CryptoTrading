package com.crypto.trading.service;

import com.crypto.trading.dto.*;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.User;
import com.crypto.trading.repository.TradeTransactionRepository;
import com.crypto.trading.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TradeService {

    private final TradeTransactionRepository tradeTransactionRepository;
    private final AggregatedPriceService aggregatedPriceService;
    private final WalletService walletService;
    private final UserService userService;

    @Transactional
    public TradeResponse tradeCrypto(TradeRequest tradeRequest){
        validateRequest(tradeRequest);
        User user = userService.getUserByUserName(tradeRequest.getUsername());

        BestPriceResponse latestBestPrice = aggregatedPriceService.getAggregatedBestPrice(tradeRequest.getSymbol());
        BigDecimal tradePrice;
        BigDecimal totalPrice;
        if ("BUY".equalsIgnoreCase(tradeRequest.getTradeAction())) {
            // BUY uses best ASK
            tradePrice = latestBestPrice.getAskPrice();
            totalPrice = tradePrice.multiply(tradeRequest.getQuantity());
            walletService.executeBuy(
                    user,
                    tradeRequest.getSymbol(),
                    tradeRequest.getQuantity(),
                    totalPrice
            );

        } else if ("SELL".equalsIgnoreCase(tradeRequest.getTradeAction())) {
            // SELL uses best BID
            tradePrice = latestBestPrice.getBidPrice();
            totalPrice = tradePrice.multiply(tradeRequest.getQuantity());
            walletService.executeSell(
                    user,
                    tradeRequest.getSymbol(),
                    tradeRequest.getQuantity(),
                    totalPrice
            );
        } else {
            throw new RuntimeException("Invalid order side");
        }

        TradeTransaction trade = TradeTransaction.builder()
                .user(walletService.getCurrentUser())
                .symbol(tradeRequest.getSymbol())
                .type(tradeRequest.getTradeAction().toUpperCase())
                .quantity(tradeRequest.getQuantity())
                .price(tradePrice)
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .build();
        tradeTransactionRepository.save(trade);

        return new TradeResponse(
                trade.getSymbol(),
                trade.getType(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTotalPrice(),
                trade.getCreatedAt()
        );
    }

    private void validateRequest(TradeRequest tradeRequest){
        if (!tradeRequest.getSymbol().equals(TradingPair.BTCUSDT.name()) && !tradeRequest.getSymbol().equals(TradingPair.ETHUSDT.name())){
            throw new RuntimeException("Pair crypto is invalid");
        }
        if (tradeRequest.getQuantity().compareTo(new BigDecimal(0)) <= 0){
            throw new RuntimeException("Quantity have to be greater than 0");
        }
    }

    public List<TradingHistory> getTradingHistoryByUserName(String userName){
        List<TradingHistory> histories = new ArrayList<>();
        User user = userService.getUserByUserName(userName);
        List<TradeTransaction> tradeTransactions = tradeTransactionRepository.findByUserOrderByCreatedAtDesc(user);

        if (!CollectionUtils.isEmpty(tradeTransactions)){
            tradeTransactions.forEach(t -> {
                TradingHistory tradingHistory = new TradingHistory();
                tradingHistory.setUser(userName);
                tradingHistory.setPrice(t.getPrice());
                tradingHistory.setSymbol(t.getSymbol());
                tradingHistory.setQuantity(t.getQuantity());
                tradingHistory.setTotalPrice(t.getTotalPrice());
                tradingHistory.setCreatedAt(t.getCreatedAt());
                histories.add(tradingHistory);
            });
        }
        return histories;
    }
}
