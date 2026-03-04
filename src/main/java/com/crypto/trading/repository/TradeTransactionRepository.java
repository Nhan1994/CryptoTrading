package com.crypto.trading.repository;

import com.crypto.trading.entity.TradeTransaction;
import com.crypto.trading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, Long> {

    List<TradeTransaction> findByUserOrderByCreatedAtDesc(User user);
}
