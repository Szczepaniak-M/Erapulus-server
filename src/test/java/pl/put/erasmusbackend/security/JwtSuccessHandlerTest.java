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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.web.server.WebFilterChain;
import pl.put.erasmusbackend.database.model.ApplicationUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtSuccessHandlerTest {

    private static final Integer ID = 1;
    private static final String TOKEN = "my.jwt.token";
    private static final String BEARER_TOKEN = "Bearer " + TOKEN;
    private static final String EMAIL = "example@gmail.com";
    private static final String BODY = "{\"status\":200," +
                                        "\"payload\":{" +
                                            "\"userId\":1," +
                                            "\"token\":\"my.jwt.token\"" +
                                        "}," +
                                        "\"message\":null}";

    @Mock
    JwtGenerator jwtGenerator;

    @Mock
    WebFilterChain webFilterChain;

    private JwtSuccessHandler jwtSuccessHandler;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        jwtSuccessHandler = new JwtSuccessHandler(objectMapper, jwtGenerator);
    }

    @Test
    void onAuthenticationSuccess_shouldGenerateResponse() {
        // given
        ApplicationUser applicationUser = new ApplicationUser().id(ID).email(EMAIL);
        when(jwtGenerator.generate(EMAIL)).thenReturn(TOKEN);
        JwtAuthenticatedUser jwtAuthenticatedUser = new JwtAuthenticatedUser(applicationUser, new SimpleGrantedAuthority("ROLE"));
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/login")
                                                                                         .header("Authorization", "Bearer myToken"));
        WebFilterExchange webFilterExchange = new WebFilterExchange(exchange, webFilterChain);

        // when
        Mono<Void> result = jwtSuccessHandler.onAuthenticationSuccess(webFilterExchange, jwtAuthenticatedUser);

        // then
        StepVerifier.create(result)
                    .verifyComplete();
        assertEquals(BEARER_TOKEN, exchange.getResponse().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, exchange.getResponse().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(BODY, exchange.getResponse().getBodyAsString().block());
    }
}
