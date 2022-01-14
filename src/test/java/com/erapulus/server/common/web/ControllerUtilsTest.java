package com.erapulus.server.common.web;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerUtilsTest {

    private final static String PATH_PARAM = "pathId";
    private final static String QUERY_PARAM = "queryParam";
    private final static String PAGE = "page";
    private final static String PAGE_SIZE = "pageSize";
    private final static Integer PATH_PARAM_VALUE = 1;
    private final static String PARAM_WRONG_VALUE = "1a";
    private final static String QUERY_PARAM_VALUE = "2";
    private final static Integer PAGE_VALUE = 3;
    private final static Integer PAGE_SIZE_VALUE = 12;
    private final static Integer PAGE_VALUE_DEFAULT = 0;
    private final static Integer PAGE_SIZE_VALUE_DEFAULT = 10;


    @Test
    void withPathParam_shouldExtractPathParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity/%s".formatted(PATH_PARAM_VALUE)))
                                             .pathVariable(PATH_PARAM, PATH_PARAM_VALUE.toString())
                                             .build();
        Function<Integer, Mono<ServerResponse>> functionWithAssert = (value) -> {
            assertEquals(PATH_PARAM_VALUE, value);
            return ServerResponse.ok().build();
        };

        // when
        Mono<ServerResponse> result = ControllerUtils.withPathParam(serverRequest, PATH_PARAM, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withPathParam_shouldReturnBadRequestWhenCantParseParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity/1"))
                                             .pathVariable(PATH_PARAM, PARAM_WRONG_VALUE)
                                             .build();
        Function<Integer, Mono<ServerResponse>> functionWithAssert = (value) -> ServerResponse.ok().build();

        // when
        Mono<ServerResponse> result = ControllerUtils.withPathParam(serverRequest, PATH_PARAM, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withQueryParam_shouldExtractQueryParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity?queryParam=2"))
                                             .queryParam(QUERY_PARAM, QUERY_PARAM_VALUE)
                                             .build();
        Function<String, Mono<ServerResponse>> functionWithAssert = (value) -> {
            assertEquals(QUERY_PARAM_VALUE, value);
            return ServerResponse.ok().build();
        };

        // when
        Mono<ServerResponse> result = ControllerUtils.withQueryParam(serverRequest, QUERY_PARAM, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withQueryParam_shouldExtractEmptyStringWhenNoGivenQueryParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity"))
                                             .build();
        Function<String, Mono<ServerResponse>> functionWithAssert = (value) -> {
            assertEquals("", value);
            return ServerResponse.ok().build();
        };

        // when
        Mono<ServerResponse> result = ControllerUtils.withQueryParam(serverRequest, QUERY_PARAM, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withPageParams_shouldExtractPageParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity?page=1&pageSize=12"))
                                             .queryParam(PAGE, PAGE_VALUE.toString())
                                             .queryParam(PAGE_SIZE, PAGE_SIZE_VALUE.toString())
                                             .build();
        Function<PageRequest, Mono<ServerResponse>> functionWithAssert = (value) -> {
            assertEquals(PAGE_VALUE, value.getPageNumber());
            assertEquals(PAGE_SIZE_VALUE, value.getPageSize());
            return ServerResponse.ok().build();
        };

        // when
        Mono<ServerResponse> result = ControllerUtils.withPageParams(serverRequest, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withPageParams_shouldUseDefaultValuesWhenParamNotGiven() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity"))
                                             .build();
        Function<PageRequest, Mono<ServerResponse>> functionWithAssert = (value) -> {
            assertEquals(PAGE_VALUE_DEFAULT, value.getPageNumber());
            assertEquals(PAGE_SIZE_VALUE_DEFAULT, value.getPageSize());
            return ServerResponse.ok().build();
        };

        // when
        Mono<ServerResponse> result = ControllerUtils.withPageParams(serverRequest, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.OK, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withPageParams_shouldReturnBadRequestWhenCantParsePageParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity?page=1a&pageSize=12"))
                                             .queryParam(PAGE, PARAM_WRONG_VALUE)
                                             .queryParam(PAGE_SIZE, PAGE_SIZE_VALUE.toString())
                                             .build();
        Function<PageRequest, Mono<ServerResponse>> functionWithAssert = (value) -> ServerResponse.ok().build();

        // when
        Mono<ServerResponse> result = ControllerUtils.withPageParams(serverRequest, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                    .verifyComplete();
    }

    @Test
    void withPageParams_shouldReturnBadRequestWhenCantParsePageSizeParam() {
        // given
        var serverRequest = MockServerRequest.builder()
                                             .uri(URI.create("/api/entity?page=1&pageSize=1a"))
                                             .queryParam(PAGE, PAGE_VALUE.toString())
                                             .queryParam(PAGE_SIZE, PARAM_WRONG_VALUE)
                                             .build();
        Function<PageRequest, Mono<ServerResponse>> functionWithAssert = (value) -> ServerResponse.ok().build();

        // when
        Mono<ServerResponse> result = ControllerUtils.withPageParams(serverRequest, functionWithAssert);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> assertEquals(HttpStatus.BAD_REQUEST, response.statusCode()))
                    .verifyComplete();
    }
}