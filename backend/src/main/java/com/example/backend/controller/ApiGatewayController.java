package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.service.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {

    @Autowired
    private GatewayService gatewayService;

    @PostMapping("/gateway")
    public ResponseEntity<?> handleRequest(
            @RequestParam String method,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestBody Map<String, Object> params
    ) {
        try {
            Object result = gatewayService.routeRequest(method, params);

            // Форматируем успешный ответ
            ApiResponse<Object> response = ApiResponse.success(result);
            response.setLimit(limit);
            response.setOffset(offset);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new GatewayException("MODULE_NOT_FOUND", "Method " +  method + " not found", null));
        }
    }
}