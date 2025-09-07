package com.example.backend.service.users.auth;

import com.example.backend.dto.user.UserDto;
import com.example.backend.model.User;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface UserService {
    UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException;
    UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException;
    User addUser(UserDto user);
}