package com.example.backend.exceptions;

public class AuthException extends GatewayException {
    public AuthException(String message) {
        super(ErrorCode.AUTH, message, null);
    }
}
