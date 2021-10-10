package pl.put.erasmusbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.put.erasmusbackend.configuration.ProjectProperties;
import pl.put.erasmusbackend.database.repository.UserRepository;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserRepository userRepository;
    private final ProjectProperties projectProperties;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        if (!isSupported(authentication)) {
            return Mono.error(new ClassCastException());
        }
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String jwt = jwtAuthenticationToken.getJwt();
        JwtParser parser = Jwts.parserBuilder()
                               .setSigningKey(Keys.hmacShaKeyFor(projectProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)))
                               .build();
        Jws<Claims> parsedJwt = parser.parseClaimsJws(jwt);
        String email = parsedJwt.getBody().getSubject();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE");
        return userRepository.findByEmail(email)
                             .map(user -> new JwtAuthenticatedUser(user, authority));
    }

    private boolean isSupported(Authentication authentication) {
        return authentication.getClass().isAssignableFrom(JwtAuthenticationToken.class);
    }

}
