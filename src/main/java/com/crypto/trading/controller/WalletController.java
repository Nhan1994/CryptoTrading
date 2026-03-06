package com.crypto.trading.controller;

import com.crypto.trading.dto.WalletResponse;
import com.crypto.trading.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    //4. API to retrieve the user’s crypto currencies wallet balance
    @GetMapping("")
    public ResponseEntity<List<WalletResponse>> getUserWallet(@RequestParam("userName") String userName,
                                                              @RequestParam(value = "currency", required = false) String currency){
        List<WalletResponse> walletResponses = walletService.getWalletBalances(userName, currency);
        return ResponseEntity.ok(walletResponses);
    }
}
