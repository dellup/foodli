package com.example.backend.service.types;

import com.example.backend.exceptions.GatewayException;

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

    public static OperationType fromString(String operation) {
        for (OperationType op : OperationType.values()) {
            if (op.getOperation().equalsIgnoreCase(operation)) {
                return op;
            }
        }
        throw new GatewayException("INVALID_OPERATION_TYPE", "Invalid operation type: " + operation, null);
    }
}
