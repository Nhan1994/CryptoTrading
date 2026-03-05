package com.crypto.trading.controller;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.service.AggregatedPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceController {

    private final AggregatedPriceService aggregatedPriceService;

    @GetMapping("/best-price")
    public ResponseEntity<BestPriceResponse> getBestAggregatedPrice(@RequestParam("symbol") String symbol){
        BestPriceResponse response = aggregatedPriceService.getAggregatedBestPrice(symbol);
        return ResponseEntity.ok(response);
    }
}
