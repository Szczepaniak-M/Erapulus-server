package com.erapulus.server.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AuthorizationFailureHandlerTest {

    private static final String BODY = "{\"status\":403," +
            "\"payload\":null," +
            "\"message\":\"forbidden\"}";

    private AuthorizationFailureHandler authorizationFailureHandler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        authorizationFailureHandler = new AuthorizationFailureHandler(objectMapper);
    }

    @Test
    void onAuthenticationFailure_shouldGenerateResponse() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login"));

        // when
        Mono<Void> result = authorizationFailureHandler.handle(exchange, new AccessDeniedException("Forbidden"));

        // then
        StepVerifier.create(result)
                    .verifyComplete();
        assertEquals(MediaType.APPLICATION_JSON_VALUE, exchange.getResponse().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(BODY, exchange.getResponse().getBodyAsString().block());
    }
}
