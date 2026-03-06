package com.crypto.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeResponse {

    private String symbol;

    private String tradeAction;

    private BigDecimal quantity;

    private BigDecimal tradePrice;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
}
