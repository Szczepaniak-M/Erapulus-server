package com.erapulus.server.security;


import com.erapulus.server.configuration.ErapulusProperties;
import com.erapulus.server.database.model.ApplicationUserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class JwtGenerator {

    private static final String ROLE = "ROLE";
    private final ErapulusProperties erapulusProperties;

    public String generate(ApplicationUserEntity subject) {
        Map<String, String> claims = new HashMap<>();
        claims.put(ROLE, subject.type().toString());
        return Jwts.builder()
                   .setClaims(claims)
                   .setIssuer(erapulusProperties.jwt().issuer())
                   .setSubject(subject.email())
                   .signWith(Keys.hmacShaKeyFor(erapulusProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)))
                   .compact();
    }
}
