package com.crypto.trading.repository;

import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, Long> {

    List<TradeTransaction> findByUserOrderByCreatedAtDesc(User user);

    Page<TradeTransaction> findByUser(User user, Pageable pageable);

    Page<TradeTransaction> findByUserAndCreatedAtBetween(
            User user,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable
    );
}
