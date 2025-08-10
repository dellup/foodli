package com.example.backend.exceptions;

import com.example.backend.utils.Log;
import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {
    private final String errorCode;
    private final Object details;

    public GatewayException(String errorCode, String message, Object details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public static GatewayException createAndLogGatewayException(String code, String message, Throwable cause) {
        GatewayException gatewayException = new GatewayException(code, message, cause);
        new Log().error(gatewayException.getMessage(), gatewayException);
        return gatewayException;
    }
}

