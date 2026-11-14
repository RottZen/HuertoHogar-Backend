package com.example.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey claveFirma;
    private final long duracionMs;

    public  JwtService(
         @Value("${app.security.jwt.secret}") String secreto,
         @Value("${app.security.jwt.expiration-ms}") long duracionExpiracionMs) {
        byte[] bytesClave = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytesClave.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe contener al menos 32 bytes UTF-8.");
        }
        this.claveFirma = Keys.hmacShaKeyFor(bytesClave);
        this.duracionExpiracionMs = duracionExpiracionMs;
    }
    public String issue(String subject, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(duracionExpiracionMs)))
                .signWith(claveFirma)
                .compact();
    }

    public String subject(String token) {
        return claims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails usuario) {
        Claims reclamosToken = claims(token);
        return reclamosToken.getSubject().equals(usuario.getUsername())
                && reclamosToken.getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(claveFirma)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
    
