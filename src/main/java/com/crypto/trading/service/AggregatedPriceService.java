package com.crypto.trading.service;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.repository.AggregatedPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AggregatedPriceService {

    private final AggregatedPriceRepository aggregatedPriceRepository;

    public BestPriceResponse getAggregatedBestPrice(String symbol){
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
