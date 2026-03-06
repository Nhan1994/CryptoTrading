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

    //3. API which allows users to trade based on the latest best aggregated price
    @PostMapping("")
    public ResponseEntity<TradeResponse> tradeCrypto(@RequestBody TradeRequest request){
        TradeResponse response = tradeService.tradeCrypto(request);
        return ResponseEntity.ok(response);
    }

    //5. API to retrieve the user trading history
    @GetMapping("/transaction-history")
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
