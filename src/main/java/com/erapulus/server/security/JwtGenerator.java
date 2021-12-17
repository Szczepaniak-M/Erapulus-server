package com.erapulus.server.security;



import java.nio.charset.StandardCharsets;

import com.erapulus.server.configuration.ErapulusProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtGenerator {

    private final ErapulusProperties erapulusProperties;

    public String generate(String subject) {
        return Jwts.builder()
                   .setIssuer(erapulusProperties.jwt().issuer())
                   .setSubject(subject)
                   .signWith(Keys.hmacShaKeyFor(erapulusProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)))
                   .compact();
    }
}
