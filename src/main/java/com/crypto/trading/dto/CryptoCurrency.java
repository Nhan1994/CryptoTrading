package com.crypto.trading.dto;

public enum CryptoCurrency {

    BTC("BTC"),
    ETH("ETH"),
    USDT("USDT");

    private final String value;

    CryptoCurrency(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
