package com.example.backend.service.users.auth.impl;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.dto.auth.RefreshTokenDto;
import com.example.backend.dto.user.UserCredentialsDto;
import com.example.backend.exceptions.ErrorCode;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.jwt.JwtService;
import com.example.backend.service.users.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail(), user.getRoles());
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken, user.getRoles());
        }
        throw createAndLogGatewayException(ErrorCode.AUTH, "Invalid refresh token", null);
    }

    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(userCredentialsDto.getEmail());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())){
                return user;
            }
        }
        throw createAndLogGatewayException(ErrorCode.AUTH, "Email or password is not correct", null);
    }

    private User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(()->
                createAndLogGatewayException(ErrorCode.AUTH,
                        String.format("User with email %s not found", email), null));
    }

}
