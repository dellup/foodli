package com.example.backend.exceptions;

public class GatewayException extends RuntimeException {
    private final String errorCode;
    private final Object details;

    public GatewayException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}

