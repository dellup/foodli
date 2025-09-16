package com.example.backend.security;

import com.example.backend.exceptions.GatewayException;
import com.example.backend.model.User;
import com.example.backend.model.role.Role;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserServiceImplTest {
    @Mock
    UserRepository repo;

    @InjectMocks
    CustomUserServiceImpl service;

    @Test
    void loadUserByUsernameFoundAndReturnsDetailsWithRoles() {
        // Arrange
        User user = new User();
        user.setEmail("u@test.com");
        user.setPassword("$2a$04$hash");
        user.setRoles(Set.of(Role.USER));
        when(repo.findByEmail("u@test.com")).thenReturn(Optional.of(user));

        // Act
        CustomUserDetails details = service.loadUserByUsername("u@test.com");

        // Assert
        assertEquals("u@test.com", details.getUsername());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsernameNotFoundAndThrowsGatewayException_REQUEST_DATA() {
        // Arrange
        when(repo.findByEmail("miss@test.com")).thenReturn(Optional.empty());

        // Act
        GatewayException ex = assertThrows(GatewayException.class,
                () -> service.loadUserByUsername("miss@test.com"));

        // Assert
        assertEquals(2000, ex.getCode());
    }
}
