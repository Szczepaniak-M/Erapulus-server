package com.erapulus.server.web.controller;

import com.erapulus.server.dto.applicationuser.ApplicationUserDto;
import com.erapulus.server.service.ApplicationUserService;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static com.erapulus.server.web.common.CommonRequestVariable.*;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static com.erapulus.server.web.controller.ControllerUtils.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Slf4j
@RestController
@AllArgsConstructor
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @NonNull
    @Operation(
            operationId = "list-application-user",
            tags = "User",
            summary = "List users",
            description = "List users by with filtering by university, type, and name",
            parameters = {
                    @Parameter(in = QUERY, name = UNIVERSITY_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = QUERY, name = TYPE_QUERY_PARAM, schema = @Schema(type = "string")),
                    @Parameter(in = QUERY, name = NAME_QUERY_PARAM, schema = @Schema(type = "string")),
                    @Parameter(in = QUERY, name = EMAIL_QUERY_PARAM, schema = @Schema(type = "string")),
                    @Parameter(in = QUERY, name = PAGE_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = QUERY, name = PAGE_SIZE_QUERY_PARAM, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApplicationUserDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listApplicationUsers(ServerRequest request) {
        return withQueryParam(request, UNIVERSITY_QUERY_PARAM,
                universityId -> withQueryParam(request, TYPE_QUERY_PARAM,
                        userType -> withQueryParam(request, EMAIL_QUERY_PARAM,
                                email -> withQueryParam(request, NAME_QUERY_PARAM,
                                        name -> withPageParams(request,
                                                pageRequest -> applicationUserService.listApplicationUsers(universityId, userType, name, email, pageRequest)
                                                                                     .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))))));
    }

    @NonNull
    @Operation(
            operationId = "delete-user",
            tags = "User",
            description = "Delete user by ID",
            summary = "Delete user",
            parameters = @Parameter(in = PATH, name = USER_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteApplicationUser(ServerRequest request) {
        return withPathParam(request, USER_PATH_PARAM,
                userId -> applicationUserService.deleteApplicationUser(userId)
                                                .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                .doOnError(e -> log.error(e.getMessage(), e))
                                                .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
