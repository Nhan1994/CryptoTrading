package com.crypto.trading.service;

import com.crypto.trading.dto.*;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.User;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.TradeTransactionRepository;
import com.crypto.trading.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
            throw new TradingBusinessException("Invalid trading action", HttpStatus.BAD_REQUEST);
        }

        TradeTransaction trade = TradeTransaction.builder()
                .user(user)
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
            throw new TradingBusinessException("Pair crypto is invalid", HttpStatus.BAD_REQUEST);
        }
        if (tradeRequest.getQuantity().compareTo(new BigDecimal(0)) <= 0){
            throw new TradingBusinessException("Quantity have to be greater than 0", HttpStatus.BAD_REQUEST);
        }
    }

    public List<TradingHistory> getTradingHistoryByUserName(String userName,
                                                            LocalDateTime startTime,
                                                            LocalDateTime endTime,
                                                            Integer page,
                                                            Integer size){
        User user = userService.getUserByUserName(userName);

        // Default behavior: latest 100
        if (page == null || size == null) {
            Pageable defaultPageable =
                    PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt"));

            return tradeTransactionRepository
                    .findByUser(user, defaultPageable)
                    .map(this::toTradingHistory)
                    .getContent();
        }

        Pageable pageable =
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<TradeTransaction> transactions;

        if (startTime != null && endTime != null) {
            transactions = tradeTransactionRepository
                    .findByUserAndCreatedAtBetween(user, startTime, endTime, pageable);
        } else {
            transactions = tradeTransactionRepository
                    .findByUser(user, pageable);
        }

        return transactions.map(this::toTradingHistory).getContent();
    }

    private TradingHistory toTradingHistory(TradeTransaction t) {
        TradingHistory history = new TradingHistory();
        history.setUser(t.getUser().getUserName());
        history.setPrice(t.getPrice());
        history.setSymbol(t.getSymbol());
        history.setQuantity(t.getQuantity());
        history.setTotalPrice(t.getTotalPrice());
        history.setCreatedAt(t.getCreatedAt());
        return history;
    }
}
