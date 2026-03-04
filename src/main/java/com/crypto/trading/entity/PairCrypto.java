package com.crypto.trading.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pair_crypto")
@Data
public class PairCrypto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String pairCrypto;

}
