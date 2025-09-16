package com.example.backend.service.users.auth.impl;

import com.example.backend.dto.user.UserDto;
import com.example.backend.exceptions.ErrorCode;
import com.example.backend.model.User;
import com.example.backend.model.role.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.users.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException {
        return null;
    }

    @Override
    @Transactional
    public User addUser(UserDto userDto){
        String email = userDto.getEmail();
        if (userRepository.findByEmail(email).isPresent()){
            throw createAndLogGatewayException(ErrorCode.REQUEST_DATA,
                    String.format("User with email %s already exists", email), null);
        }
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Set.of(Role.USER));
        return userRepository.save(user);
    }

}