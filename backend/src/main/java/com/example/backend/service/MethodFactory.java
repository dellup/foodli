package com.example.backend.service;

import com.example.backend.service.types.OperationType;

public interface MethodFactory {
    AbstractMethod createMethod(OperationType operation);
}
