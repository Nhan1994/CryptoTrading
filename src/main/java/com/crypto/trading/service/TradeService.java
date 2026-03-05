package com.crypto.trading.service;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.dto.TradeRequest;
import com.crypto.trading.dto.TradeResponse;
import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.repository.TradeTransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class TradeService {

    private final TradeTransactionRepository tradeTransactionRepository;
    private final AggregatedPriceService aggregatedPriceService;
    private final WalletService walletService;

    public TradeResponse tradeCrypto(TradeRequest tradeRequest){
        validateRequest(tradeRequest);

        BestPriceResponse latestBestPrice = aggregatedPriceService.getAggregatedBestPrice(tradeRequest.getSymbol());
        BigDecimal tradePrice;
        BigDecimal totalPrice;
        if ("BUY".equalsIgnoreCase(tradeRequest.getTradeAction())) {
            // BUY uses best ASK
            tradePrice = latestBestPrice.getAskPrice();
            totalPrice = tradePrice.multiply(tradeRequest.getQuantity());
            walletService.executeBuy(
                    tradeRequest.getUsername(),
                    tradeRequest.getSymbol(),
                    tradeRequest.getQuantity(),
                    totalPrice
            );

        } else if ("SELL".equalsIgnoreCase(tradeRequest.getTradeAction())) {
            // SELL uses best BID
            tradePrice = latestBestPrice.getBidPrice();
            totalPrice = tradePrice.multiply(tradeRequest.getQuantity());
            walletService.executeSell(
                    tradeRequest.getUsername(),
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
        if (!tradeRequest.getSymbol().equals("ETHUSDT") && !tradeRequest.getSymbol().equals("BTCUSDT")){
            throw new RuntimeException("Pair crypto is invalid");
        }
        if (tradeRequest.getQuantity().compareTo(new BigDecimal(0)) <= 0){
            throw new RuntimeException("Quantity have to be greater than 0");
        }
    }
}
