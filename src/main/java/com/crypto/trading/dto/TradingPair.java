package com.crypto.trading.dto;

public enum TradingPair {

    BTCUSDT("BTCUSDT"),
    ETHUSDT("ETHUSDT");

    private final String value;
    TradingPair(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
