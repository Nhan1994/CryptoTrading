package com.crypto.trading.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletResponse {

    private String userName;
    private String currency;
    private BigDecimal balance;
}
