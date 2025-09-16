package com.example.backend.service.utils.auth.role;

import com.example.backend.exceptions.GatewayException;
import com.example.backend.model.role.Role;
import com.example.backend.service.utils.auth.role.RoleGuard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class RoleGuardTest {

    private final RoleGuard guard = new RoleGuard();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireAllRolesPresent() {
        // Arrange
        var auth = new UsernamePasswordAuthenticationToken(
                "user", null,
                Set.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act + Assert
        assertDoesNotThrow(() -> guard.require(Role.USER, Role.ADMIN));
    }

    @Test
    void requireMissingRoleThrowsGatewayException_RIGHTS() {
        // Arrange
        var auth = new UsernamePasswordAuthenticationToken(
                "user", null,
                Set.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act + Assert
        GatewayException ex = assertThrows(GatewayException.class,
                () -> guard.require(Role.ADMIN));
        assertEquals(54, ex.getCode());
    }
}
