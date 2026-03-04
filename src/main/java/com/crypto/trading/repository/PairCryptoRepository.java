package com.crypto.trading.repository;

import com.crypto.trading.entity.PairCrypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PairCryptoRepository extends JpaRepository<PairCrypto, Long> {

    Optional<PairCrypto> findByPairSymbol(String symbol);


}
