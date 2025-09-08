package com.example.backend.dto.user;

import com.example.backend.model.role.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class UserCredentialsDto {
    @NotBlank(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password should not be empty")
    private String password;
    private Set<Role> roles;
}