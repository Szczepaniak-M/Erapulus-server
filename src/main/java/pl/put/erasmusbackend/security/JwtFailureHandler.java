package pl.put.erasmusbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtFailureHandler implements ServerAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException e) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(ServerResponseFactory.createHttpBadCredentialsResponse()))
                   .map(body -> response.bufferFactory().wrap(body))
                   .flatMap(dataBuffer -> response.writeWith(Mono.just(dataBuffer)));
    }
}
