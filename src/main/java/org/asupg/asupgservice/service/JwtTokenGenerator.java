package org.asupg.asupgservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtTokenGenerator {

    // Expiry time in minutes
    @Value("${security.jwt.expireInMinutes:15}")
    private Long tokenExpireIn;

    private final SecretKey secretKey;

    public JwtTokenGenerator(@Value("${security.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        var authorities = authentication.getAuthorities()
                .stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        return Jwts.builder()
                .subject(authentication.getName())
                .claim("roles", authorities)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tokenExpireIn * 60 * 1000))
                .signWith(secretKey)
                .compact();
    }

}
