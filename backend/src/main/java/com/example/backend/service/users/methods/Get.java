package com.example.backend.service.users.methods;

import com.example.backend.model.role.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractGet;
import com.example.backend.service.PrototypeComponent;
import com.example.backend.service.utils.auth.jwt.JwtAuthenticated;
import com.example.backend.service.utils.auth.role.RequireRoles;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@JwtAuthenticated
@PrototypeComponent
@RequireRoles({Role.USER})
public class Get extends AbstractGet {
    private final UserRepository userRepository;
    private int id;
    @Override
    protected List<Optional<?>> exec() {
        return List.of(Optional.of(userRepository.findById((long) id)));
    }
}
