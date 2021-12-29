package com.erapulus.server.security;


import com.erapulus.server.configuration.ErapulusProperties;
import com.erapulus.server.database.model.ApplicationUserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class JwtGenerator {

    private static final String ROLE = "ROLE";
    private static final int DAY = 24 * 60 * 60 * 1000;
    private final ErapulusProperties erapulusProperties;

    public String generate(ApplicationUserEntity subject) {
        Map<String, String> claims = new HashMap<>();
        claims.put(ROLE, subject.type().toString());
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuer(erapulusProperties.jwt().issuer())
                   .setSubject(subject.email())
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + DAY))
                   .signWith(Keys.hmacShaKeyFor(erapulusProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)))
                   .compact();
    }
}
