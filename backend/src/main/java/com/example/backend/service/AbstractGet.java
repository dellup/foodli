package com.example.backend.service;

import com.example.backend.service.filters.FilterCondition;

import java.util.List;
import java.util.Optional;

public abstract class AbstractGet extends AbstractMethod {
    private Integer limit;
    private Integer offset;
    private List<FilterCondition> filters;
    private Integer totalCount; // Для хранения общего количества записей до пагинации

    @Override
    protected abstract List<Optional<?>> exec();


}
