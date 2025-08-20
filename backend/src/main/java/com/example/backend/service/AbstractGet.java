package com.example.backend.service;

import java.util.List;
import java.util.Optional;

public abstract class AbstractGet extends AbstractMethod {
    private Integer limit;
    private Integer offset;

    @Override
    protected abstract List<Optional<?>> exec();
}
