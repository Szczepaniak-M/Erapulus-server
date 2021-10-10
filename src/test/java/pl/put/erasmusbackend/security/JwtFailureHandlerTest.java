package pl.put.erasmusbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JwtFailureHandlerTest {

    private static final String BODY = "{\"status\":401," +
                                        "\"payload\":null," +
                                        "\"message\":\"bad.credentials\"}";

    @Mock
    WebFilterChain webFilterChain;

    private JwtFailureHandler jwtFailureHandler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        jwtFailureHandler = new JwtFailureHandler(objectMapper);
    }

    @Test
    void onAuthenticationFailure_shouldGenerateResponse() {
        // given
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login")
                                                                                         .header("Authorization", "Bearer myToken"));
        WebFilterExchange webFilterExchange = new WebFilterExchange(exchange, webFilterChain);

        // when
        Mono<Void> result = jwtFailureHandler.onAuthenticationFailure(webFilterExchange, new BadCredentialsException("Bad credentials"));

        // then
        StepVerifier.create(result)
                    .verifyComplete();
        assertEquals(MediaType.APPLICATION_JSON_VALUE, exchange.getResponse().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(BODY, exchange.getResponse().getBodyAsString().block());
    }
}
