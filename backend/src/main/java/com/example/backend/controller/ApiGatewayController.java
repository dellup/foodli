package com.example.backend.controller;

import com.example.backend.dto.request.ApiRequest;
import com.example.backend.dto.response.ApiError;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.service.GatewayService;
import com.example.backend.uttils.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {



    @PostMapping("/gateway")
    public ResponseEntity<?> handleRequest(@RequestBody ApiRequest request) {
        new Log().info("Начало обработки запроса в Gateway");
        try {
            var result = GatewayService.call(request);

            // Форматируем успешный ответ
            var response = ApiResponse.success(result);
            return ResponseEntity.ok(response);

        } catch (GatewayException e) {
            // Обработка специфических исключений
            ApiError error = new ApiError("MODULE_NOT_FOUND", "Method not found: " + request.getMethodName());
            var errorResponse = ApiResponse.error(Collections.singletonList(error));

            // Логирование исключения
            new Log().error(error.getMessage(), e);

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (NumberFormatException e) {
            // Обработка ошибок преобразования значений limit или offset
            ApiError error = new ApiError("INVALID_PARAMS", "Invalid limit or offset values");
            var errorResponse = ApiResponse.error(Collections.singletonList(error));

            // Логирование исключения
            new Log().error(error.getMessage(), e);

            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Обработка прочих ошибок
            ApiError error = new ApiError("SERVER_ERROR", "Internal server error");
            var errorResponse = ApiResponse.error(Collections.singletonList(error));

            // Логирование исключения
            new Log().error(error.getMessage(), e);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
