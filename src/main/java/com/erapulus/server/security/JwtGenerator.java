package com.erapulus.server.security;



import java.nio.charset.StandardCharsets;

import com.erapulus.server.configuration.ProjectProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtGenerator {

    private final ProjectProperties projectProperties;

    public String generate(String subject) {
        return Jwts.builder()
                   .setIssuer(projectProperties.jwt().issuer())
                   .setSubject(subject)
                   .signWith(Keys.hmacShaKeyFor(projectProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)))
                   .compact();
    }
}
