package com.example.backend.service.users.methods;

import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractGet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class Get extends AbstractGet {
    private final UserRepository userRepository;
    private int id;
    @Override
    protected List<Optional<?>> exec() {
        return List.of(Optional.of(userRepository.findById((long) id)));
    }
}
