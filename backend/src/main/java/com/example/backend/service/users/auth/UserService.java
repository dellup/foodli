package com.example.backend.service.users.auth;

import com.example.backend.dto.user.UserDto;
import com.example.backend.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {
    UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException;
    UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException;
    User addUser(@Valid @NotNull UserDto user);
}