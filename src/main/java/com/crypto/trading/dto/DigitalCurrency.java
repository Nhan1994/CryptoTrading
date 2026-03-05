package com.crypto.trading.dto;

public enum DigitalCurrency {

    BTC("BTC"),
    ETH("ETH"),
    USDT("USDT");

    private final String value;

    DigitalCurrency(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
