package com.example.backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse {
    private List<Optional<?>> result;
    private Integer total;
    private Integer nextOffset;
    private Integer limit;
    private Integer offset;
    private ApiError error;
}
