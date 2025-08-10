package com.example.backend.service.users.methods;

import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class Edit extends AbstractMethod {
    private final UserRepository userRepository;

    @Override
    protected List<Optional<?>> exec(Map<String, Object> params) {
        var user = userRepository.findById(((Integer) params.get("id")).longValue());
        user.get().setUsername(params.get("username").toString());
        return List.of(user);
    }
}
