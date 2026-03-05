package com.crypto.trading.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TradingBusinessExceptionHandler {

    @ExceptionHandler(TradingBusinessException.class)
    public ResponseEntity<ResponseError> handleBusinessException(
            TradingBusinessException ex,
            HttpServletRequest request) {

        ResponseError error = new ResponseError(
                ex.getStatus(),
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(error, ex.getStatus());
    }
}
