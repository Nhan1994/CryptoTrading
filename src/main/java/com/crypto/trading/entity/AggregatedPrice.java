package com.crypto.trading.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aggregated_price")
@Data
@Builder
public class AggregatedPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, precision = 19)
    private BigDecimal bestBid;

    @Column(nullable = false, precision = 19)
    private BigDecimal bestAsk;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
