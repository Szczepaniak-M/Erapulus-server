package pl.put.erasmusbackend.security;



import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.configuration.ProjectProperties;

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
