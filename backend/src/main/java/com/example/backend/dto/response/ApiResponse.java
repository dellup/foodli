package com.example.backend.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ApiResponse<T> {
    private T result;
    private Integer total;
    private Integer nextOffset;
    private Integer limit;
    private Integer offset;
    private List<ApiError> errors;

    public static <T> ApiResponse<T> success(T result) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setResult(result);
        return response;
    }

    public static <T> ApiResponse<T> error(List<ApiError> errors) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setErrors(errors);
        return response;
    }
}
