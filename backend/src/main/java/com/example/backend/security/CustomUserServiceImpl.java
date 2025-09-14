package com.example.backend.security;

import com.example.backend.exceptions.ErrorCode;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).map(CustomUserDetails::new)
                .orElseThrow(() -> createAndLogGatewayException(ErrorCode.REQUEST_DATA,
                        "Username aren't found during authentication", new UsernameNotFoundException(username)));
    }
}