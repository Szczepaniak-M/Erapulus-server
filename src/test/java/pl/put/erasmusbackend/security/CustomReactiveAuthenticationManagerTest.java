package pl.put.erasmusbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.put.erasmusbackend.database.model.ApplicationUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomReactiveAuthenticationManagerTest {

     private final static String EMAIL = "example@gmail.com";

    @Mock
    private JwtReactiveAuthenticationManager jwtManager;

    @Mock
    private UserDetailsRepositoryReactiveAuthenticationManager userDetailsManager;

    private CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    @BeforeEach
    void setUp() {
        customReactiveAuthenticationManager = new CustomReactiveAuthenticationManager(jwtManager, userDetailsManager);
    }

    @Test
    void authenticate_shouldReturnAuthenticationWhenJwtAuthenticationSuccess() {
        // given
        ApplicationUser user = new ApplicationUser().email(EMAIL);
        Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
        JwtAuthenticatedUser jwtAuthenticatedUser = new JwtAuthenticatedUser(user, new SimpleGrantedAuthority("ROLE"));
        when(jwtManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(jwtAuthenticatedUser));

        // when
        Mono<Authentication> authFromAuthenticationManager = customReactiveAuthenticationManager.authenticate(authentication);

        // then
        StepVerifier.create(authFromAuthenticationManager)
                .expectSubscription()
                .assertNext(auth -> assertEquals(jwtAuthenticatedUser, auth))
                .verifyComplete();
    }

    @Test
    void authenticate_shouldReturnAuthenticationWhenUserDetailsAuthenticationSuccess() {
        // given
        Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken("principal", "credentials");
        when(jwtManager.authenticate(any(Authentication.class))).thenReturn(Mono.error(new ClassCastException()));
        when(userDetailsManager.authenticate(any(Authentication.class))).thenReturn(Mono.just(usernamePasswordAuthenticationToken));

        // when
        Mono<Authentication> authFromAuthenticationManager = customReactiveAuthenticationManager.authenticate(authentication);

        // then
        StepVerifier.create(authFromAuthenticationManager)
                    .expectSubscription()
                    .assertNext(auth -> assertEquals(usernamePasswordAuthenticationToken, auth))
                    .verifyComplete();
    }

    @Test
    void authenticate_shouldReturnAuthenticationWhenAuthenticationFail() {
        // given
        Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
        when(jwtManager.authenticate(any(Authentication.class))).thenReturn(Mono.error(new ClassCastException()));
        when(userDetailsManager.authenticate(any(Authentication.class))).thenReturn(Mono.error(new BadCredentialsException("Invalid Credentials")));

        // when
        Mono<Authentication> result = customReactiveAuthenticationManager.authenticate(authentication);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(BadCredentialsException.class)
                    .verify();
    }
}
