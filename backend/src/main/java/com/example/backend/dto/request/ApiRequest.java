package com.example.backend.dto.request;

import com.example.backend.service.types.OperationType;
import lombok.Data;

import java.util.Map;

@Data
public class ApiRequest {
    private OperationType operation;
    private String serviceName;
    private String methodName;
    private Map<String, Object> params;
    // Тут будут храниться offset, limit и прочее
    private Map<String, Object> selectorParams;
//
}
