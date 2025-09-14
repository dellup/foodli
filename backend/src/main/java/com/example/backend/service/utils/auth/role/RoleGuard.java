package com.example.backend.service.utils.auth.role;

import com.example.backend.exceptions.ErrorCode;
import com.example.backend.model.role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Component
public class RoleGuard {
    public void require(Role... roles) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            throw createAndLogGatewayException(ErrorCode.RIGHTS,
                    "Empty user role", null);
        }
        var have = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        boolean ok = Arrays.stream(roles).allMatch(r -> have.contains("ROLE_" + r.name()));
        if (!ok) throw createAndLogGatewayException(ErrorCode.RIGHTS,
                "Unacceptable user role to access", null);
    }
}