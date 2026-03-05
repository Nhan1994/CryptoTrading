package com.crypto.trading.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequest {

    private String username;

    private String symbol;

    private String tradeAction;

    private BigDecimal quantity;
}
