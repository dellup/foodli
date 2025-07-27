package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse {
    private List<ApiError> errors;
}
