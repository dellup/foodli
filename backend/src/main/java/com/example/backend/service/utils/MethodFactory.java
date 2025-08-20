package com.example.backend.service.utils;

import com.example.backend.service.AbstractMethod;
import com.example.backend.service.types.OperationType;

public interface MethodFactory {
    AbstractMethod createMethod(String serviceName, String methodName, OperationType operation);
}
