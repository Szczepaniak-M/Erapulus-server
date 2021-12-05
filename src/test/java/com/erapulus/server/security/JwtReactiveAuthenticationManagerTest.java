package com.erapulus.server.security;

import com.erapulus.server.configuration.ProjectProperties;
import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtReactiveAuthenticationManagerTest {

    private static final String EMAIL = "example@gmail.com";
    private static final String ISSUER = "issuer";
    private static final String SECRET = "my-incredibly-strong-and-secure-secret";

    @Mock
    UserRepository userRepository;

    @Mock
    ProjectProperties projectProperties;

    private JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @BeforeEach
    void setUp() {
        jwtReactiveAuthenticationManager = new JwtReactiveAuthenticationManager(userRepository, projectProperties);
    }

    @Test
    void authenticate_shouldReturnJwtAuthenticatedUserWhenJwtAuthenticationTokenPassed() {
        // given
        ApplicationUserEntity user = ApplicationUserEntity.builder().type(UserType.STUDENT).email(EMAIL).build();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Mono.just(user));
        when(projectProperties.jwt()).thenReturn(new ProjectProperties.JwtProperties(ISSUER, SECRET));
        JwtGenerator jwtGenerator = new JwtGenerator(projectProperties);
        String jwt = jwtGenerator.generate(EMAIL);
        Authentication authentication = new JwtAuthenticationToken(jwt);

        // when
        Mono<Authentication> result = jwtReactiveAuthenticationManager.authenticate(authentication);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(auth -> {
                        assertEquals(user, auth.getPrincipal());
                        assertTrue(auth.isAuthenticated());
                    })
                    .verifyComplete();
    }

    @Test
    void authenticate_shouldReturnErrorWhenNoJwtAuthenticationTokenPassed() {
        // given
        Authentication authentication = new TestingAuthenticationToken("principal", "credentials");

        // when
        Mono<Authentication> result = jwtReactiveAuthenticationManager.authenticate(authentication);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(ClassCastException.class)
                    .verify();
    }
}
