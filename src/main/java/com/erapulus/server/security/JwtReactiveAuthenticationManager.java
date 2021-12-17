package com.erapulus.server.security;

import com.erapulus.server.configuration.ErapulusProperties;
import com.erapulus.server.database.repository.UserRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserRepository userRepository;
    private final ErapulusProperties erapulusProperties;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (!isSupported(authentication)) {
            return Mono.error(new ClassCastException());
        }
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        String jwt = jwtAuthenticationToken.getJwt();
        JwtParser parser = Jwts.parserBuilder()
                               .setSigningKey(Keys.hmacShaKeyFor(erapulusProperties.jwt().secret().getBytes(StandardCharsets.UTF_8)))
                               .build();

        return Mono.just(jwt)
                   .map(token -> parser.parseClaimsJws(token).getBody().getSubject())
                   .flatMap(userRepository::findByEmail)
                   .map(user -> new JwtAuthenticatedUser(user, new SimpleGrantedAuthority(user.type().toString())))
                   .onErrorResume(e -> Mono.error(new BadCredentialsException("bad.token")))
                   .map(JwtAuthenticatedUser::asAuthentication);
    }

    private boolean isSupported(Authentication authentication) {
        return authentication.getClass().isAssignableFrom(JwtAuthenticationToken.class);
    }

}
