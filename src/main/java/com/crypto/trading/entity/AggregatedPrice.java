package com.crypto.trading.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "aggregated_price")
@Data
public class AggregatedPrice {

    @Id
    private String pair;

    private double bidPrice; // For SELL
    private double askPrice; // For BUY
    private LocalDateTime timestamp;
}
