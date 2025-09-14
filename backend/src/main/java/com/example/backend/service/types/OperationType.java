package com.example.backend.service.types;

import com.example.backend.exceptions.ErrorCode;
import com.example.backend.exceptions.GatewayException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    ADD("add"),
    GET("get"),
    EDIT("edit"),
    DELETE("del");

    private final String operation;

    OperationType(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }

    @JsonCreator
    public static OperationType fromString(String operation) {
        for (OperationType op : OperationType.values()) {
            if (op.getOperation().equalsIgnoreCase(operation)) {
                return op;
            }
        }
        throw new GatewayException(ErrorCode.REQUEST_TYPE, "Invalid operation type: " + operation, null);
    }
}
