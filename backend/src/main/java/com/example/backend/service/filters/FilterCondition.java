package com.example.backend.service.filters;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class FilterCondition {
    private String field;
    private String operator;
    private List<Object> values;
}