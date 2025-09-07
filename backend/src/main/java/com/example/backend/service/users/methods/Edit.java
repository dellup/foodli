package com.example.backend.service.users.methods;

import com.example.backend.model.role.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AbstractMethod;
import com.example.backend.service.PrototypeComponent;
import com.example.backend.service.utils.auth.jwt.JwtAuthenticated;
import com.example.backend.service.utils.auth.role.RequireRoles;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@JwtAuthenticated
@PrototypeComponent
@RequireRoles({Role.USER, Role.ADMIN})
public class Edit extends AbstractMethod {
    private final UserRepository userRepository;
    private int id;
    private String email;

    @Override
    protected List<Optional<?>> exec() {
        var user = userRepository.findById((long) id);
        user.get().setEmail(email);
        return List.of(user);
    }
}
