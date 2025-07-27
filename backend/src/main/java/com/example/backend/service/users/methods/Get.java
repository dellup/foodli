package com.example.backend.service.users.methods;
import com.example.backend.service.AbstractMethod;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
class Get extends AbstractMethod {
    private Long userId;
    @Override
    protected List<Optional<?>> exec() {
        return null;
    }
}
