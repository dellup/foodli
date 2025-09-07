package com.example.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JwtAuthenticationDto {
    private String token;
    private String refreshToken;
}