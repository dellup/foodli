package com.example.backend.security.jwt;

import com.example.backend.dto.auth.JwtAuthenticationDto;
import com.example.backend.model.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.example.backend.exceptions.GatewayException.createAndLogGatewayException;

@Component
public class JwtService {

    @Value("${jwt.secret.key}")
    private String jwtSecret;

    private static final Duration REFRESH_ABSOLUTE_TTL = Duration.ofDays(30);

    public JwtAuthenticationDto generateAuthToken(String email, Set<Role> roles) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email, roles));
        Date refreshExp = Date.from(LocalDateTime.now()
                .plus(REFRESH_ABSOLUTE_TTL)
                .atZone(ZoneId.systemDefault()).toInstant());
        jwtDto.setRefreshToken(generateRefreshToken(email, refreshExp));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken, Set<Role> roles) {
        // 1) проверяем подпись + извлекаем claims
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload();

        Date originalExp = claims.getExpiration();
        if (originalExp == null) {
            throw createAndLogGatewayException("TOKEN_EXPIRED_EXCEPTION", "Refresh token has no exp", null);
        }
        // 2) не продлеваем! — если exp уже прошёл, рефрешить нельзя
        if (originalExp.before(new Date())) {
            throw createAndLogGatewayException("TOKEN_EXPIRED_EXCEPTION", "Refresh token expired", null);
        }

        // 3) выдаём новый access и НОВЫЙ refresh с тем же exp
        JwtAuthenticationDto dto = new JwtAuthenticationDto();
        dto.setToken(generateJwtToken(email, roles));
        dto.setRefreshToken(generateRefreshToken(email, originalExp));
        return dto;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException e){
            throw createAndLogGatewayException("EXPIRED_TOKEN", "Jwt token has expired", e);
        } catch (UnsupportedJwtException e){
            throw createAndLogGatewayException("UNSUPPORTED_TOKEN", "Jwt token has unsupported", e);
        } catch (MalformedJwtException e){
            throw createAndLogGatewayException("MALFORMED_TOKEN", "Jwt token has malformed", e);
        } catch (SecurityException e){
            throw createAndLogGatewayException("SECURITY_PROBLEM", "An security error occurred during token validation", e);
        } catch (Exception e){
            throw createAndLogGatewayException("INVALID_TOKEN", "Jwt token is invalid", e);
        }
    }

    private String generateJwtToken(String email, Set<Role> roles) {
        Date date = Date.from(LocalDateTime.now()
                .plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .claim("roles", roles.stream().map(Role::name).toList())
                .expiration(date)
                .issuedAt(new Date())
                .signWith(getSingInKey())
                .compact();
    }
    private String generateRefreshToken(String email, Date exp) {
        return Jwts.builder()
                .subject(email)
                .expiration(exp)
                .issuedAt(new Date())
                .signWith(getSingInKey())
                .compact();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims c = Jwts.parser().verifyWith(getSingInKey()).build()
                .parseSignedClaims(token).getPayload();
        Object raw = c.get("roles");
        if (raw instanceof List<?> list) return list.stream().map(String::valueOf).toList();
        return List.of();
    }

    public Duration getRemainingLifetime(String token) {
        Claims claims = Jwts.parser().verifyWith(getSingInKey()).build()
                .parseSignedClaims(token).getPayload();
        Date exp = claims.getExpiration();
        long now = System.currentTimeMillis();
        if (exp == null) throw createAndLogGatewayException("TOKEN_EXPIRED_EXCEPTION", "Refresh token has no exp", null);
        long ms = exp.getTime() - now;
        return Duration.ofMillis(Math.max(ms, 0));
    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}