package com.example.backend.service.users.auth;

import com.example.backend.dto.user.UserDto;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.model.User;
import com.example.backend.model.role.Role;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.users.auth.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserServiceTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void addUserSuccess() {
        // Arrange
        UserDto dto = new UserDto();
        dto.setEmail("newuser@test.com");
        dto.setPassword("Qwerty123");

        when(userRepository.findByEmail("newuser@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Qwerty123")).thenReturn("encodedSecret");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User saved = userService.addUser(dto);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("newuser@test.com", saved.getEmail());
        assertEquals("encodedSecret", saved.getPassword());
        assertEquals(Set.of(Role.USER), saved.getRoles());
    }

    @Test
    void addUserEmailAlreadyExistsThrowsGatewayException_REQUEST_DATA() {
        // Arrange
        UserDto dto = new UserDto();
        dto.setEmail("aboba@test.com");
        dto.setPassword("Qwerty123");

        when(userRepository.findByEmail("aboba@test.com"))
                .thenReturn(Optional.of(new User()));

        // Act
        GatewayException ex = assertThrows(GatewayException.class,
                () -> userService.addUser(dto));

        // Assert
        assertEquals(2000, ex.getCode());
        verify(userRepository, never()).save(any());
    }
}
