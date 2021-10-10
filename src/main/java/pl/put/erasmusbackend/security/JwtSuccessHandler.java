package pl.put.erasmusbackend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.ApplicationUser;
import pl.put.erasmusbackend.dto.TokenDto;
import pl.put.erasmusbackend.web.common.ResponseFactory;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtGenerator jwtGenerator;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ApplicationUser applicationUser = (ApplicationUser) authentication.getPrincipal();
        String token = jwtGenerator.generate(applicationUser.email());
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        TokenDto tokenDto = TokenDto.builder().userId(applicationUser.id()).token(token).build();
        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(ResponseFactory.createHttpSuccessResponse(tokenDto)))
                   .map(body -> response.bufferFactory().wrap(body))
                   .flatMap(dataBuffer -> response.writeWith(Mono.just(dataBuffer)));
    }
}
