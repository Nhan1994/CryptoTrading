package com.crypto.trading.service;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.dto.TradingPair;
import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.exception.TradingBusinessException;
import com.crypto.trading.repository.AggregatedPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregatedPriceService {

    private final AggregatedPriceRepository aggregatedPriceRepository;

    public BestPriceResponse getAggregatedBestPrice(String symbol){
        if (!TradingPair.BTCUSDT.getValue().equals(symbol) && !TradingPair.ETHUSDT.getValue().equals(symbol)){
            log.error("Invalid symbol input");
            throw new TradingBusinessException("Invalid symbol input", HttpStatus.BAD_REQUEST);
        }
        BestPriceResponse bestPriceResponse = new BestPriceResponse();
        Optional<AggregatedPrice> aggregatedPrice = aggregatedPriceRepository.findTopBySymbolOrderByCreatedAtDesc(symbol);
        aggregatedPrice.ifPresent(p -> {
            bestPriceResponse.setSymbol(p.getSymbol());
            bestPriceResponse.setBidPrice(p.getBestBid());
            bestPriceResponse.setAskPrice(p.getBestAsk());
        });
        return bestPriceResponse;
    }
}
