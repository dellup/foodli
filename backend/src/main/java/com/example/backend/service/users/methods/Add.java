package com.example.backend.service.users.methods;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class Add extends AbstractMethod {
    private final UserRepository userRepository;

    @Override
    protected List<Optional<?>> exec(Map<String, Object> params) {
        var username = String.valueOf(params.get("username"));
        var user = new User();
        user.setUsername(username);
        userRepository.save(user);
        return List.of(Optional.of(user));
    }
}
