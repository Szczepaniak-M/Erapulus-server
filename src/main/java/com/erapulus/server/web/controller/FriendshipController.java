package com.erapulus.server.web.controller;

import com.erapulus.server.dto.FriendshipDecisionDto;
import com.erapulus.server.dto.FriendshipRequestDto;
import com.erapulus.server.dto.FriendshipResponseDto;
import com.erapulus.server.dto.StudentListDto;
import com.erapulus.server.service.FriendshipService;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.erapulus.server.web.common.CommonRequestVariable.*;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static com.erapulus.server.web.controller.ControllerUtils.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Slf4j
@RestController
@AllArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    @NonNull
    @Operation(
            operationId = "list-student-friends",
            tags = "Friendship",
            summary = "List friends",
            description = "List friends by student ID with filtering by name",
            parameters = {
                    @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = QUERY, name = NAME_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = QUERY, name = PAGE_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = QUERY, name = PAGE_SIZE_QUERY_PARAM, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentListDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listFriends(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withQueryParam(request, NAME_QUERY_PARAM,
                        name -> withPageParams(request,
                                pageRequest -> friendshipService.listFriends(studentId, name, pageRequest)
                                                                .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                .doOnError(e -> log.error(e.getMessage(), e))
                                                                .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "list-friend-requests",
            tags = "Friendship",
            summary = "List friend requests",
            description = "List friend requests by student ID",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentListDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listFriendRequests(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> friendshipService.listFriendRequests(studentId)
                                              .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                              .doOnError(e -> log.error(e.getMessage(), e))
                                              .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "add-friend",
            tags = "Friendship",
            summary = "Send friend request",
            description = "Send friend request",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FriendshipRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = FriendshipResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> addFriend(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> request.bodyToMono(FriendshipRequestDto.class)
                                    .flatMap(friendRequest -> friendshipService.addFriendRequest(friendRequest, studentId))
                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                    .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("duplicated.request"))
                                    .onErrorResume(IllegalArgumentException.class, e -> ServerResponseFactory.createHttpConflictResponse("other.request"))
                                    .doOnError(e -> log.error(e.getMessage(), e))
                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "handle-friend-request",
            tags = "Friendship",
            summary = "Accept or decline friend request",
            description = "Accept or decline  friend request",
            parameters = {
                    @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FRIEND_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FriendshipDecisionDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = FriendshipResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> handleFriendshipRequest(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, FRIEND_PATH_PARAM,
                        friendId -> request.bodyToMono(FriendshipDecisionDto.class)
                                           .flatMap(decision -> friendshipService.handleFriendshipRequest(decision, studentId, friendId))
                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                           .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                           .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                           .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("friend"))
                                           .doOnError(e -> log.error(e.getMessage(), e))
                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "delete-friend",
            tags = "Friendship",
            summary = "Delete user from friends",
            description = "Delete user from friends",
            parameters = {
                    @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FRIEND_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteFriend(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, FRIEND_PATH_PARAM,
                        friendId -> friendshipService.deleteFriend(studentId, friendId)
                                                     .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));

    }
}
