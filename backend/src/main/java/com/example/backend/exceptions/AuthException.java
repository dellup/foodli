package com.example.backend.exceptions;

public class AuthException extends GatewayException {
    public AuthException(String message) {
        super("AUTH_ERROR", message, null);
    }
}
