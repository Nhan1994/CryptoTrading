package com.crypto.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class CryptoPrice {

    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
}
