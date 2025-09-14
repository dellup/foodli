package com.example.backend.exceptions;

import com.example.backend.utils.Log;
import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {
    private final String errorName;
    private final int code;
    private final Object details;

    public GatewayException(ErrorCode errorCode, String message, Object details) {
        super(message);
        this.errorName = errorCode.name();
        this.code = errorCode.getCode();
        this.details = details;
    }

    public static GatewayException createAndLogGatewayException(ErrorCode errorCode, String message, Throwable cause) {
        GatewayException gatewayException = new GatewayException(errorCode, message, cause);
        new Log().error("[" + errorCode.getCode() + " " + errorCode.name() + "] " +
                gatewayException.getMessage(), gatewayException);
        return gatewayException;
    }

}

