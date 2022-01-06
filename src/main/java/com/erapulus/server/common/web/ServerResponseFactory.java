package com.erapulus.server.common.web;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerResponseFactory {

    public static Mono<ServerResponse> createHttpSuccessResponse(Object payload) {
        return ServerResponse.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.OK.value())
                                                                           .payload(payload)
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpCreatedResponse(Object payload) {
        return ServerResponse.status(HttpStatus.CREATED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.CREATED.value())
                                                                           .payload(payload)
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpNoContentResponse() {
        return ServerResponse.status(HttpStatus.NO_CONTENT)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.NO_CONTENT.value())
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpForbiddenErrorResponse() {
        String message = "forbidden";
        return ServerResponse.status(HttpStatus.FORBIDDEN)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.FORBIDDEN.value())
                                                                           .message(message)
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpBadRequestCantParseErrorResponse() {
        String message = "bad.request;cannot.parse.parameter";
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.BAD_REQUEST.value())
                                                                           .message(message)
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpBadRequestConstraintViolationErrorResponse(ConstraintViolationException exception) {
        String message = "bad.request;" + getConstraintViolationReason(exception);
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.BAD_REQUEST.value())
                                                                           .message(message)
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpBadRequestNoBodyFoundErrorResponse() {
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.BAD_REQUEST.value())
                                                                           .message("bad.request;not.found.body")
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpBadRequestInvalidCredentialsErrorResponse() {
        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.BAD_REQUEST.value())
                                                                           .message("bad.request;invalid.credentials")
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpUnauthorizedResponse() {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(createHttpUnauthorizedResponseBody()));
    }

    public static ResponseTemplate<Object> createHttpUnauthorizedResponseBody() {
        return ResponseTemplate.builder()
                               .status(HttpStatus.UNAUTHORIZED.value())
                               .message("bad.credentials")
                               .build();
    }

    public static ResponseTemplate<Object> createHttpForbiddenResponseBody() {
        return ResponseTemplate.builder()
                               .status(HttpStatus.FORBIDDEN.value())
                               .message("forbidden")
                               .build();
    }

    public static Mono<ServerResponse> createHttpNotFoundResponse(NoSuchElementException e) {
        return ServerResponse.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.NOT_FOUND.value())
                                                                           .message(e.getMessage() + ".not.found")
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpConflictResponse(String objectName) {
        return ServerResponse.status(HttpStatus.CONFLICT)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.CONFLICT.value())
                                                                           .message(objectName + ".conflict")
                                                                           .build()));
    }

    public static Mono<ServerResponse> createHttpInternalServerErrorResponse() {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(BodyInserters.fromValue(ResponseTemplate.builder()
                                                                           .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                                           .message("internal.server.error")
                                                                           .build()));
    }

    private static String getConstraintViolationReason(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                        .stream()
                        .map(constraintViolation -> {
                            String[] splitPath = constraintViolation.getPropertyPath().toString().split("\\.");
                            String key = splitPath[splitPath.length - 1];
                            String reason = constraintViolation.getMessage().replace(" ", ".");
                            return key + "." + reason;
                        }).collect(Collectors.joining(";"));
    }
}
