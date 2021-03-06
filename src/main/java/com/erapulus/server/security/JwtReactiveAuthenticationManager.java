package com.erapulus.server.security;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.applicationuser.database.ApplicationUserRepository;
import com.erapulus.server.common.configuration.ErapulusProperties;
import com.erapulus.server.common.database.UserType;
import io.jsonwebtoken.Claims;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ApplicationUserRepository applicationUserRepository;
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
                   .map(token -> parser.parseClaimsJws(token).getBody())
                   .flatMap(this::validateToken)
                   .flatMap(applicationUserRepository::findByEmail)
                   .map(user -> new JwtAuthenticatedUser(user, grantRoles(user)))
                   .onErrorResume(e -> Mono.error(new BadCredentialsException("bad.token")))
                   .map(JwtAuthenticatedUser::asAuthentication);
    }

    private Mono<String> validateToken(Claims claims) {
        long currentTime = System.currentTimeMillis();
        if (currentTime < claims.getIssuedAt().getTime()
                || currentTime > claims.getExpiration().getTime()
                || !Objects.equals(claims.getIssuer(), erapulusProperties.jwt().issuer())) {
            return Mono.error(new BadCredentialsException("bad.token"));
        }
        return Mono.just(claims.getSubject());
    }

    private boolean isSupported(Authentication authentication) {
        return authentication.getClass().isAssignableFrom(JwtAuthenticationToken.class);
    }

    private List<SimpleGrantedAuthority> grantRoles(ApplicationUserEntity user) {
        List<SimpleGrantedAuthority> roles = new LinkedList<>();
        roles.add(new SimpleGrantedAuthority(user.type().toString()));
        if (user.universityId() != null) {
            roles.add(new SimpleGrantedAuthority("UNIVERSITY_" + user.universityId()));
        }
        if (user.type() == UserType.ADMINISTRATOR) {
            roles.add(new SimpleGrantedAuthority("UNIVERSITY_null"));
        }
        if (user.type() == UserType.STUDENT) {
            roles.add(new SimpleGrantedAuthority("STUDENT_" + user.id()));
        }
        return roles;
    }

}
