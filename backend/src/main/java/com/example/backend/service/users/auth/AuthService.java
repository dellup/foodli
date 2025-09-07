package com.example.backend.service.users.auth;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.dto.auth.RefreshTokenDto;
import com.example.backend.dto.user.UserCredentialsDto;

import javax.naming.AuthenticationException;

public interface AuthService {
    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException;
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;
}
