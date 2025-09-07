package com.example.backend.service.users.reg;

import com.example.backend.dto.user.UserDto;
import com.example.backend.service.AbstractMethod;
import com.example.backend.service.PrototypeComponent;
import com.example.backend.service.users.auth.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@PrototypeComponent
public class Add extends AbstractMethod {
    private final UserService userService;

    private String email;
    private String password;

    @Override
    protected List<Optional<?>> exec() {
        var user = new UserDto();
        user.setEmail(email);
        user.setPassword(password);
        return List.of(Optional.of(userService.addUser(user)));
    }
}
