package com.example.backend.service.users.auth;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.dto.auth.RefreshTokenDto;
import com.example.backend.dto.user.UserCredentialsDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import javax.naming.AuthenticationException;

@Validated
public interface AuthService {
    JwtAuthenticationDto signIn(@Valid @NotNull UserCredentialsDto userCredentialsDto) throws AuthenticationException;
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;
}
