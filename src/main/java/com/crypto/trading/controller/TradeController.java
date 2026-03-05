package com.crypto.trading.controller;

import com.crypto.trading.dto.TradeRequest;
import com.crypto.trading.dto.TradeResponse;
import com.crypto.trading.dto.TradingHistory;
import com.crypto.trading.service.TradeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<List<TradingHistory>> getTradingHistory(
            @RequestParam String userName,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        List<TradingHistory> tradingHistories =
                tradeService.getTradingHistoryByUserName(
                        userName,
                        startTime,
                        endTime,
                        page,
                        size);

        return ResponseEntity.ok(tradingHistories);
    }
}
