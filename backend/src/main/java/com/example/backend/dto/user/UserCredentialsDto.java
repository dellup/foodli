package com.example.backend.dto.user;

import com.example.backend.model.role.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserCredentialsDto {
    private String email;
    private String password;
    private Set<Role> roles;
}