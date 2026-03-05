package com.crypto.trading.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_transaction")
@Data
@Builder
public class TradeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String symbol;

    private String type; //BUY or SELL

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private LocalDateTime createdAt;
}
