package com.erapulus.server.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import com.erapulus.server.database.model.ApplicationUserEntity;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityContextUtils {

    public static Mono<ApplicationUserEntity> getUserAuthenticationData() {
        return ReactiveSecurityContextHolder.getContext()
                                            .map(SecurityContext::getAuthentication)
                                            .filter(Authentication::isAuthenticated)
                                            .map(Authentication::getPrincipal)
                                            .map(ApplicationUserEntity.class::cast);
    }

    public static <T> Mono<T> withSecurityContext(Function<ApplicationUserEntity, Mono<T>> function) {
        return getUserAuthenticationData()
                .flatMap(function);
    }
}
