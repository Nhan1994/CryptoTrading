package com.crypto.trading.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade_transaction")
@Data
public class TradeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String pair;

    private String type; //BUY or SELL

    private double amount;

    private double price;

    private double totalUsdt;

    private LocalDateTime timestamp;
}
