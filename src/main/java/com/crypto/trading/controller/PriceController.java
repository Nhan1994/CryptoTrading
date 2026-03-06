package com.crypto.trading.controller;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.service.AggregatedPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceController {

    private final AggregatedPriceService aggregatedPriceService;

    //2. API to retrieve the latest best aggregated price
    @GetMapping("/best-price")
    public ResponseEntity<BestPriceResponse> getBestAggregatedPrice(@RequestParam("symbol") String symbol){
        BestPriceResponse response = aggregatedPriceService.getAggregatedBestPrice(symbol);
        return ResponseEntity.ok(response);
    }
}
