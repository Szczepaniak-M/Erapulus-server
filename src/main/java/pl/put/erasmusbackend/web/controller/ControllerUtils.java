package pl.put.erasmusbackend.web.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerUtils {

    public static Mono<ServerResponse> withPathParam(ServerRequest request, String paramName, Function<Integer, Mono<ServerResponse>> function) {
        return Mono.just(request.pathVariable(paramName))
                   .map(Integer::parseInt)
                   .flatMap(function)
                   .onErrorResume(NumberFormatException.class, ServerResponseFactory::createHttpBadRequestCantParseToIntegerErrorResponse);
    }
}
