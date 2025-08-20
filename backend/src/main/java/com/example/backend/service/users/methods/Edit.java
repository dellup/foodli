package com.example.backend.service.users.methods;

import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Scope("prototype")
public class Edit extends AbstractMethod {
    private final UserRepository userRepository;
    private int id;
    private String username;

    @Override
    protected List<Optional<?>> exec() {
        var user = userRepository.findById((long) id);
        user.get().setUsername(username);
        return List.of(user);
    }
}
