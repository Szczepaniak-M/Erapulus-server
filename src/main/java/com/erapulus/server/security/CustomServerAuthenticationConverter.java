package com.erapulus.server.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

@Component
public class CustomServerAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange serverWebExchange) {
        String bearerToken = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
        return Mono.justOrEmpty(getJwtAuthenticationToken(bearerToken));
    }

    private Optional<JwtAuthenticationToken> getJwtAuthenticationToken(String bearerToken) {
        JwtAuthenticationToken jwtAuthenticationToken = null;
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.replace("Bearer ", "");
            jwtAuthenticationToken = new JwtAuthenticationToken(jwt);
        }
        return Optional.ofNullable(jwtAuthenticationToken);
    }
}
