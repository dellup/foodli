package com.example.backend.security.jwt;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.exceptions.GatewayException;
import com.example.backend.model.role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        byte[] key = new byte[32];
        for (int i = 0; i < key.length; i++) key[i] = 0x01;
        String b64 = Base64.getEncoder().encodeToString(key);
        ReflectionTestUtils.setField(jwtService, "jwtSecret", b64);
    }

    @Test
    void generateAndValidateAccessTokenOk() {
        // Arrange
        String email = "user@test.com";
        Set<Role> roles = Set.of(Role.USER);

        // Act
        JwtAuthenticationDto dto = jwtService.generateAuthToken(email, roles);
        boolean valid = jwtService.validateJwtToken(dto.getToken());

        // Assert
        assertNotNull(dto.getToken());
        assertNotNull(dto.getRefreshToken());
        assertTrue(valid);
        assertTrue(jwtService.validateJwtToken(dto.getToken()));
        assertEquals(roles.toString(),
                jwtService.getRolesFromToken(dto.getToken()).toString());
        assertEquals(email, jwtService.getEmailFromToken(dto.getToken()));
    }

    @Test
    void validateJwtTokenMalformedAndThrowsGatewayException_REQUEST_VALUE() {
        // Arrange
        String malformed = "tralalelo_tralala";

        // Act
        GatewayException ex = assertThrows(GatewayException.class,
                () -> jwtService.validateJwtToken(malformed));

        // Assert
        assertEquals(2003, ex.getCode());
    }
}
