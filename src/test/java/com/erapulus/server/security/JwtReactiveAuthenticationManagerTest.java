package com.erapulus.server.security;

import com.erapulus.server.configuration.ErapulusProperties;
import com.erapulus.server.database.model.ApplicationUserEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.database.repository.ApplicationUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtReactiveAuthenticationManagerTest {

    private static final String EMAIL = "example@gmail.com";
    private static final String ISSUER = "issuer";
    private static final String SECRET = "my-incredibly-strong-and-secure-secret";
    private static final int USER_ID = 1;
    private static final int UNIVERSITY_ID = 2;


    @Mock
    ApplicationUserRepository applicationUserRepository;

    @Mock
    ErapulusProperties erapulusProperties;

    private JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager;

    @BeforeEach
    void setUp() {
        jwtReactiveAuthenticationManager = new JwtReactiveAuthenticationManager(applicationUserRepository, erapulusProperties);
    }

    @Test
    void authenticate_shouldReturnJwtAuthenticatedUserWhenJwtAuthenticationTokenPassed() {
        // given
        ApplicationUserEntity user = ApplicationUserEntity.builder().type(UserType.STUDENT).email(EMAIL).build();
        when(applicationUserRepository.findByEmail(EMAIL)).thenReturn(Mono.just(user));
        when(erapulusProperties.jwt()).thenReturn(new ErapulusProperties.JwtProperties(ISSUER, SECRET));
        JwtGenerator jwtGenerator = new JwtGenerator(erapulusProperties);
        String jwt = jwtGenerator.generate(user);
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

    @Test
    void authenticate_shouldGenerateProperAuthorityWhenJwtTokenPassed() {
        // given
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID).type(UserType.STUDENT).email(EMAIL).universityId(UNIVERSITY_ID).build();
        when(applicationUserRepository.findByEmail(EMAIL)).thenReturn(Mono.just(user));
        when(erapulusProperties.jwt()).thenReturn(new ErapulusProperties.JwtProperties(ISSUER, SECRET));
        JwtGenerator jwtGenerator = new JwtGenerator(erapulusProperties);
        String jwt = jwtGenerator.generate(user);
        Authentication authentication = new JwtAuthenticationToken(jwt);
        List<GrantedAuthority> resultRoles= List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_2"),
                new SimpleGrantedAuthority("STUDENT_1"));

        // when
        Mono<Authentication> result = jwtReactiveAuthenticationManager.authenticate(authentication);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(auth -> assertEquals(resultRoles, auth.getAuthorities()))
                    .verifyComplete();
    }
}
