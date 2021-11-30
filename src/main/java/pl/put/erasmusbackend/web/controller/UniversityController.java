package pl.put.erasmusbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.dto.*;
import pl.put.erasmusbackend.service.UniversityService;
import pl.put.erasmusbackend.service.exception.NoSuchUniversityException;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

import static pl.put.erasmusbackend.web.common.CommonPathVariable.UNIVERSITY_PATH_PARAM;
import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;
import static pl.put.erasmusbackend.web.controller.ControllerUtils.withPathParam;


@Controller
@AllArgsConstructor
public class UniversityController {

    private static final String UNIVERSITY = "university";
    private final UniversityService universityService;

    @NonNull
    @Operation(
            operationId = "list-universities",
            tags = "University",
            description = "List universities",
            summary = "List universities",
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = UniversityListDto.class)))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listUniversities(ServerRequest request) {
        return universityService.listUniversities()
                                .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "create-university",
            tags = "University",
            description = "Create university",
            summary = "Create university",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BuildingRequestDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createUniversity(ServerRequest request) {
        return request.bodyToMono(UniversityRequestDto.class)
                      .flatMap(universityService::createUniversity)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "get-university",
            tags = "University",
            description = "Get university by ID",
            summary = "Get university by ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getUniversityById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> universityService.getUniversityById(universityId)
                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                 .onErrorResume(NoSuchUniversityException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(UNIVERSITY))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-university",
            tags = "University",
            description = "Update university",
            summary = "Update university",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BuildingRequestDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = BuildingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(UniversityRequestDto.class)
                                       .flatMap(universityDto -> universityService.updateUniversity(universityId, universityDto))
                                       .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                       .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                       .onErrorResume(NoSuchUniversityException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(UNIVERSITY))
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "delete-university",
            tags = "University",
            description = "Delete university",
            summary = "Delete university by ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "204", description = NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> universityService.deleteUniversity(universityId)
                                                 .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                 .onErrorResume(NoSuchUniversityException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(UNIVERSITY))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
