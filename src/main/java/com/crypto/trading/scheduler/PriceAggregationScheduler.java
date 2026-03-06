package com.crypto.trading.scheduler;

import com.crypto.trading.dto.CryptoPrice;
import com.crypto.trading.dto.TradingPair;
import com.crypto.trading.dto.binance.BinanceResponse;
import com.crypto.trading.dto.houbi.HoubiPrice;
import com.crypto.trading.dto.houbi.HoubiResponse;
import com.crypto.trading.entity.AggregatedPrice;
import com.crypto.trading.repository.AggregatedPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceAggregationScheduler {

    @Value("${binance.fetch-price.url}")
    private String binanceUrl;

    @Value("${houbi.fetch-price.url}")
    private String houbiUrl;

    private final RestTemplate restTemplate;
    private final AggregatedPriceRepository aggregatedPriceRepository;

    @Scheduled(fixedRate = 10000)
    public void fetchAggregatedPrice(){
        try {
            List<CryptoPrice> prices = new ArrayList<>();
            prices.addAll(fetchPriceFromBinance());
            prices.addAll(fetchPriceFromHoubi());

            for (TradingPair symbol : TradingPair.values()) {
                List<CryptoPrice> symbolPrices = prices.stream()
                        .filter(p -> symbol.getValue().equalsIgnoreCase(p.getSymbol()))
                        .toList();

                if (symbolPrices.isEmpty()) {
                    log.warn("No price found for {}", symbol);
                    continue;
                }

                BigDecimal bestBid = symbolPrices.stream()
                        .map(CryptoPrice::getBidPrice)
                        .max(BigDecimal::compareTo)
                        .orElseThrow();

                BigDecimal bestAsk = symbolPrices.stream()
                        .map(CryptoPrice::getAskPrice)
                        .min(BigDecimal::compareTo)
                        .orElseThrow();

                AggregatedPrice aggregatedPrice = AggregatedPrice.builder()
                        .symbol(symbol.getValue())
                        .bestBid(bestBid)
                        .bestAsk(bestAsk)
                        .createdAt(LocalDateTime.now())
                        .build();


                aggregatedPriceRepository.save(aggregatedPrice);

                log.info("Aggregated {} -> BestBid: {}, BestAsk: {}",
                        symbol, bestBid, bestAsk);
            }

        } catch (Exception ex) {
            log.error("Price aggregation failed", ex);
        }
    }

    private List<CryptoPrice> fetchPriceFromBinance(){
        BinanceResponse[] responses;
        ResponseEntity<BinanceResponse[]> binanceResponse = restTemplate.getForEntity(binanceUrl, BinanceResponse[].class);
        if (binanceResponse.getStatusCode().is2xxSuccessful() && binanceResponse.getBody() != null){
            responses = binanceResponse.getBody();
            assert responses != null;
            return Arrays.stream(responses)
                    .map(r -> new CryptoPrice(
                            r.getSymbol(),
                            new BigDecimal(r.getBidPrice()),
                            new BigDecimal(r.getAskPrice())
                    ))
                    .toList();
        }
        return Collections.emptyList();
    }

    private List<CryptoPrice> fetchPriceFromHoubi(){
        HoubiResponse houbiResponse;
        ResponseEntity<HoubiResponse> response = restTemplate.getForEntity(houbiUrl, HoubiResponse.class);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                && response.getBody().getStatus().equals("ok")){
            houbiResponse = response.getBody();
            List<HoubiPrice> prices = houbiResponse.getData();
            return prices.stream()
                    .map(p -> new CryptoPrice(
                            p.getSymbol(),
                            new BigDecimal(p.getBid()),
                            new BigDecimal(p.getAsk())
                    ))
                    .toList();
        }
        return Collections.emptyList();
    }
}
