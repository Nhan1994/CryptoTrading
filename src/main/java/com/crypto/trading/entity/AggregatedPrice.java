package com.crypto.trading.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aggregated_price")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal bestBid;

    @Column(nullable = false, precision = 19, scale = 8)
    private BigDecimal bestAsk;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
