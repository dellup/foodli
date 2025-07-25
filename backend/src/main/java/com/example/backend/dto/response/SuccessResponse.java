package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SuccessResponse<T> {
    private T result;
    private Integer total;
    private Integer nextOffset;
    private Integer limit;
    private Integer offset;
}
