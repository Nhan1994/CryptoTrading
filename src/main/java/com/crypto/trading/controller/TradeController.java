package com.crypto.trading.controller;

import com.crypto.trading.dto.BestPriceResponse;
import com.crypto.trading.dto.TradeRequest;
import com.crypto.trading.dto.TradeResponse;
import com.crypto.trading.dto.TradingHistory;
import com.crypto.trading.service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
@AllArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("")
    public ResponseEntity<TradeResponse> tradeCrypto(@RequestBody TradeRequest request){
        TradeResponse response = tradeService.tradeCrypto(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction-histories")
    public ResponseEntity<List<TradingHistory>> getTradingHistory(@RequestParam(value = "userName") String userName){
        List<TradingHistory> tradingHistories = tradeService.getTradingHistoryByUserName(userName);
        return ResponseEntity.ok(tradingHistories);
    }
}
