package com.example.backend.service.users.methods;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractGet;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Scope("prototype")
public class Get extends AbstractGet {
    private final UserRepository userRepository;
    @Override
    protected List<Optional<?>> exec() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(Optional::of)
                .collect(Collectors.toList());
    }
}
