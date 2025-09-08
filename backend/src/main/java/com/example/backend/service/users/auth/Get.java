package com.example.backend.service.users.auth;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.dto.user.UserCredentialsDto;
import com.example.backend.service.AbstractGet;
import com.example.backend.service.PrototypeComponent;
import com.example.backend.utils.Log;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.naming.AuthenticationException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@RequiredArgsConstructor
@PrototypeComponent
public class Get extends AbstractGet {
    private String email;
    private String password;
    private final AuthService authService;

    @Override
    protected List<Optional<?>> exec() {
        try {
            UserCredentialsDto userCredentialsDto = new UserCredentialsDto();
            userCredentialsDto.setEmail(email);
            userCredentialsDto.setPassword( password);
            JwtAuthenticationDto jwtAuthenticationDto = authService.signIn(userCredentialsDto);
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", jwtAuthenticationDto.getRefreshToken())
                    .httpOnly(true)              // недоступна JS
                    // todo: включить в продакшн .secure(true)                // только по HTTPS
                    .maxAge(Duration.ofDays(60)) // срок жизни refresh
                    .path("/api")
                    .sameSite("Lax")             // чаще всего достаточно; если кросс-домен — "None" + HTTPS
                    .build();

            // Достаём текущий HttpServletResponse и ставим cookie
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletResponse response = attrs.getResponse();
                if (response != null) {
                    response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                } else {
                    new Log().warn("HttpServletResponse is null, cannot set refresh cookie");
                }
            } else {
                new Log().warn("RequestAttributes is null, cannot set refresh cookie");
            }
            var tokenDto = new JwtAuthenticationDto();
            tokenDto.setToken(jwtAuthenticationDto.getToken());
            return List.of(Optional.of(tokenDto));
        } catch (ConstraintViolationException e) {
            // 2003 — ERROR_CODE_REQUEST_VALUE (некорректное значение поля)
            String msg = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .reduce((a,b) -> a + "; " + b)
                    .orElse("Validation error");
            throw createAndLogGatewayException("2003", msg, e);

        } catch (AuthenticationException e) {
            // 53 — ERROR_CODE_AUTH
            throw createAndLogGatewayException("53", "Missing password or email", e);
        }

    }
}
