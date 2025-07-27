package com.example.backend.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class SuccessResponse {
    private List<Optional<?>> result;
    private Integer total;
    private Integer nextOffset;
    private Integer limit;
    private Integer offset;
}
