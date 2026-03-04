package com.crypto.trading.repository;

import com.crypto.trading.entity.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {

    Optional<AggregatedPrice> findTopBySymbolOrderByCreatedAtDesc(String symbol);
}
