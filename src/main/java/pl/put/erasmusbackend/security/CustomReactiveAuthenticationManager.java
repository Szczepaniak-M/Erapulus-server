package pl.put.erasmusbackend.security;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

@AllArgsConstructor
class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;
    private final UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return jwtReactiveAuthenticationManager.authenticate(authentication)
                                               .onErrorResume(e -> userDetailsRepositoryReactiveAuthenticationManager.authenticate(authentication))
                                               .flatMap(auth -> Mono.just(auth)
                                                                    .contextWrite(context -> ReactiveSecurityContextHolder.withAuthentication(auth)));
    }
}
