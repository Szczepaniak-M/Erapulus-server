package com.erapulus.server.security;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.erapulus.server.common.web.CommonRequestVariable.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtReactiveAuthorizationManagerTest {

    private static final int UNIVERSITY_ID_1 = 1;
    private static final int UNIVERSITY_ID_2 = 2;
    private static final int USER_ID_1 = 3;
    private static final int USER_ID_2 = 4;

    @Test
    void check_shouldGrantAccessWhenOnlyUserTypeRoleIsRequired() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), null);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertTrue(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldNotGrantAccessWhenWrongUserTypeRole() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.ADMINISTRATOR), null);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertFalse(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldGrantAccessWhenUniversityIdRoleIsRequired() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Map<String, Object> variables = new HashMap<>();
        variables.put(UNIVERSITY_PATH_PARAM, UNIVERSITY_ID_1);
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), UNIVERSITY_PATH_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertTrue(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldNotGrantAccessWhenOtherUniversityIdRoleIsRequired() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Map<String, Object> variables = new HashMap<>();
        variables.put(UNIVERSITY_PATH_PARAM, UNIVERSITY_ID_2);
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), UNIVERSITY_PATH_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertFalse(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldGrantAccessWhenUniversityIdRoleIsRequiredBaseOnQueryParam() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint?university=1")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Map<String, Object> variables = new HashMap<>();
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), UNIVERSITY_QUERY_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertTrue(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldNotGrantAccessWhenOtherUniversityIdRoleIsRequiredBasedOnQueryParam() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint?university=2")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Map<String, Object> variables = new HashMap<>();
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), UNIVERSITY_QUERY_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertFalse(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldGrantAccessWhenStudentIdRoleIsRequired() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Map<String, Object> variables = new HashMap<>();
        variables.put(STUDENT_PATH_PARAM, USER_ID_1);
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), STUDENT_PATH_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertTrue(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldNotGrantAccessWhenOtherStudentIdRoleIsRequired() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.STUDENT).universityId(UNIVERSITY_ID_2).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("STUDENT"),
                new SimpleGrantedAuthority("UNIVERSITY_1"),
                new SimpleGrantedAuthority("STUDENT_3"));
        Map<String, Object> variables = new HashMap<>();
        variables.put(STUDENT_PATH_PARAM, USER_ID_2);
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT), STUDENT_PATH_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertFalse(decision.isGranted()))
                    .verifyComplete();
    }

    @Test
    void check_shouldNotGrantAccessWhenNoStudentIdRoleIsRequired() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/endpoint")
                                                                                         .header("Authorization", "Bearer myToken"));
        ApplicationUserEntity user = ApplicationUserEntity.builder().id(USER_ID_1).type(UserType.EMPLOYEE).universityId(UNIVERSITY_ID_1).build();
        List<SimpleGrantedAuthority> authority = List.of(new SimpleGrantedAuthority("EMPLOYEE"), new SimpleGrantedAuthority("UNIVERSITY_1"));
        Map<String, Object> variables = new HashMap<>();
        variables.put(STUDENT_PATH_PARAM, USER_ID_2);
        Mono<Authentication> jwtAuthenticatedUser = Mono.just(new JwtAuthenticatedUser(user, authority));
        AuthorizationContext authorizationContext = new AuthorizationContext(exchange, variables);


        // when
        JwtReactiveAuthorizationManager jwtReactiveAuthorizationManager = new JwtReactiveAuthorizationManager(List.of(UserType.STUDENT, UserType.EMPLOYEE), STUDENT_PATH_PARAM);
        Mono<AuthorizationDecision> result = jwtReactiveAuthorizationManager.check(jwtAuthenticatedUser, authorizationContext);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(decision -> assertFalse(decision.isGranted()))
                    .verifyComplete();
    }
}
