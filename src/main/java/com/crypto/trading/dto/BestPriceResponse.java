package com.crypto.trading.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BestPriceResponse {

    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
}
