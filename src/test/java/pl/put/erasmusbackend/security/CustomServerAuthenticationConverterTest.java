package pl.put.erasmusbackend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class CustomServerAuthenticationConverterTest {

    private CustomServerAuthenticationConverter customServerAuthenticationConverter;

    @BeforeEach
    void setUp() {
        customServerAuthenticationConverter = new CustomServerAuthenticationConverter();
    }

    @Test
    void convert_shouldReturnJwtWhenTokenInRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login")
                                                                                         .header("Authorization", "Bearer myToken"));

        // when
        Mono<Authentication> result = customServerAuthenticationConverter.convert(exchange);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(jwtToken -> {
                        assertNotNull(jwtToken.getCredentials());
                        assertFalse(jwtToken.isAuthenticated());
                    })
                    .verifyComplete();
    }

    @Test
    void convert_shouldReturnEmptyWhenNoTokenInRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login"));

        // when
        Mono<Authentication> result = customServerAuthenticationConverter.convert(exchange);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    @Test
    void convert_shouldReturnEmptyWhenNotBearerTokenInRequest() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login")
                                                                                         .header("Authorization", "myToken"));
        // when
        Mono<Authentication> result = customServerAuthenticationConverter.convert(exchange);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }
}
