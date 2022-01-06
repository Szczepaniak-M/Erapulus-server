package com.erapulus.server.common.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerUtils {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;

    public static Mono<ServerResponse> withPathParam(ServerRequest request, String paramName, Function<Integer, Mono<ServerResponse>> function) {
        return Mono.just(request.pathVariable(paramName))
                   .map(Integer::parseInt)
                   .flatMap(function)
                   .onErrorResume(NumberFormatException.class, e -> ServerResponseFactory.createHttpBadRequestCantParseErrorResponse());
    }

    public static Mono<ServerResponse> withQueryParam(ServerRequest request, String paramName, Function<String, Mono<ServerResponse>> function) {
        return Mono.just(request.queryParam(paramName))
                   .map(optional -> optional.orElse(""))
                   .flatMap(function);
    }

    public static Mono<ServerResponse> withPageParams(ServerRequest request, Function<PageRequest, Mono<ServerResponse>> function) {
        return Mono.just(request.queryParam(CommonRequestVariable.PAGE_QUERY_PARAM))
                   .map(optional -> optional.map(Integer::parseInt)
                                            .orElse(DEFAULT_PAGE))
                   .map(page -> {
                       int size = request.queryParam(CommonRequestVariable.PAGE_SIZE_QUERY_PARAM)
                                         .map(Integer::parseInt)
                                         .orElse(DEFAULT_PAGE_SIZE);
                       return PageRequest.of(page, size);
                   })
                   .flatMap(function)
                   .onErrorResume(NumberFormatException.class, e -> ServerResponseFactory.createHttpBadRequestCantParseErrorResponse());
    }
}
