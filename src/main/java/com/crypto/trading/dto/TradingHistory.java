package com.crypto.trading.dto;

import com.crypto.trading.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradingHistory {
    private String user;

    private String symbol;

    private String type;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
}
