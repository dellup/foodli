package com.example.backend.service.utils.auth.jwt;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.exceptions.ErrorCode;
import com.example.backend.security.CustomUserDetails;
import com.example.backend.security.CustomUserServiceImpl;
import com.example.backend.security.jwt.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Component
@RequiredArgsConstructor
public class JwtAuthGuard {

    private final JwtService jwtService;
    private final CustomUserServiceImpl customUserService;

    public void enforce() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) throw new AuthenticationCredentialsNotFoundException("No request context");
        HttpServletRequest request = attrs.getRequest();
        HttpServletResponse response = attrs.getResponse();

        // 1) пробуем access из Authorization
        String token = getAccessTokenFromAuthHeader(request);
        if (token != null && jwtService.validateJwtToken(token)) {
            setAuthentication(token);
            return;
        }

        // 2) access невалиден/отсутствует — пробуем refresh из cookie
        String refreshToken = getRefreshTokenFromCookies(request);
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            // обновляем access и refresh)
            String email = jwtService.getEmailFromToken(refreshToken);
            JwtAuthenticationDto refreshed = jwtService.refreshBaseToken(jwtService.getEmailFromToken(refreshToken),
                    refreshToken, customUserService.loadUserByUsername(email).user().getRoles());

            // ставим аутентификацию новым access
            setAuthentication(refreshed.getToken());

            // отдать новый access в ответе (чтобы фронт мог перехватить и обновить заголовок)
            if (response != null) {
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + refreshed.getToken());
                Duration remaining = jwtService.getRemainingLifetime(refreshed.getRefreshToken()); // exp - now
                response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("refresh_token", refreshed.getRefreshToken())
                        .httpOnly(true)
                       // todo: включить на проде .secure(true)
                        .path("/api")
                        .sameSite("Lax")
                        .maxAge(remaining)
                        .build().toString());
                return;
            }
        }
        // 3) нет access и не смогли освежить — запрет
        throw createAndLogGatewayException(ErrorCode.AUTH,
                "Invalid or missing access/refresh token", null);
    }

    private String getAccessTokenFromAuthHeader(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if ("refresh_token".equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    private void setAuthentication(String accessToken) {
        String email = jwtService.getEmailFromToken(accessToken);
        CustomUserDetails userDetails = customUserService.loadUserByUsername(email);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
