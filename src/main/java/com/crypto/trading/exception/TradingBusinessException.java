package com.crypto.trading.exception;

import org.springframework.http.HttpStatus;

public class TradingBusinessException extends RuntimeException{

    private final HttpStatus status;

    public TradingBusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
