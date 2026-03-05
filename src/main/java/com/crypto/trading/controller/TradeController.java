package com.crypto.trading.controller;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.dto.TradeRequest;
import com.crypto.trading.dto.TradeResponse;
import com.crypto.trading.service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/trade")
@AllArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("")
    public ResponseEntity<TradeResponse> tradeCrypto(@RequestBody TradeRequest request){
        TradeResponse response = tradeService.tradeCrypto(request);
        return ResponseEntity.ok(response);
    }
}
