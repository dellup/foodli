package com.example.backend.controller;

import com.example.backend.dto.request.ApiRequest;
import com.example.backend.dto.response.ApiError;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.exceptions.ErrorCode;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiGatewayController {
    private final GatewayService gatewayService;

    @PostMapping("/gateway")
    public ResponseEntity<?> handleRequest(@RequestBody ApiRequest request) {
        try {
            var response = gatewayService.call(request);

            // Форматируем успешный ответ
            return ResponseEntity.ok(response);

        } catch (GatewayException e) {
            // Обработка специфических исключений
            ApiError error = new ApiError(e.getCode(), e.getErrorName(), e.getMessage());
            ApiResponse response = new ApiResponse();
            response.setError(error);

            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            // Обработка прочих ошибок
            ApiError error = new ApiError(ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                    ErrorCode.INTERNAL_SERVER_ERROR.name(), e.getMessage());
            ApiResponse response = new ApiResponse();
            response.setError(error);

            return ResponseEntity.status(500).body(response);
        }
    }
}
