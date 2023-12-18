package com.example.server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 часа
    private static final long INITIALIZATION_TIME = 60 * 1000; // 1 мин

    @Value("${SECRET_KEY}")
    private String secretKey;

    public String generateToken(Integer id, String login, String role) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        return Jwts.builder()
                .claim("id", id)
                .claim("login", login)
                .claim("role", role)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims verifyToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())).build();
            return parser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;  // Возвращаем null в случае неверного или истекшего токена
        }
    }

    public String getRoleFromToken(String token) {
        Claims claims = verifyToken(token);
        return claims == null ? null : claims.get("role", String.class);
    }

    // Для инициализации проекта
    public String generateTokenForAccountant() {
        String role = "accountant"; // Устанавливаем роль "accountant" зафиксированно

        Date expirationDate = new Date(System.currentTimeMillis() + INITIALIZATION_TIME);
        return Jwts.builder()
                .claim("role", role)
                .setExpiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }
}
