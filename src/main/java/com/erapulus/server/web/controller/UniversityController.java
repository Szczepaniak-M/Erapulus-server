package com.erapulus.server.web.controller;

import com.erapulus.server.dto.BuildingResponseDto;
import com.erapulus.server.dto.UniversityListDto;
import com.erapulus.server.web.common.CommonRequestVariable;
import com.erapulus.server.web.common.OpenApiConstants;
import com.erapulus.server.web.common.ServerResponseFactory;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.erapulus.server.dto.UniversityRequestDto;
import com.erapulus.server.dto.UniversityResponseDto;
import com.erapulus.server.service.UniversityService;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;


@RestController
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
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = UniversityListDto.class)))),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listUniversities(ServerRequest request) {
        return universityService.listEntities()
                                .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "create-university",
            tags = "University",
            description = "Create university",
            summary = "Create university",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UniversityRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createUniversity(ServerRequest request) {
        return request.bodyToMono(UniversityRequestDto.class)
                      .flatMap(universityService::createEntity)
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
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getUniversityById(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> universityService.getEntityById(universityId)
                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                 .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(UNIVERSITY))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-university",
            tags = "University",
            description = "Update university",
            summary = "Update university",
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UniversityRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = BuildingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateUniversity(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(UniversityRequestDto.class)
                                       .flatMap(universityDto -> universityService.updateEntity(universityDto, universityId))
                                       .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                       .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                       .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(UNIVERSITY))
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "delete-university",
            tags = "University",
            description = "Delete university",
            summary = "Delete university by ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = OpenApiConstants.NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteUniversity(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> universityService.deleteEntity(universityId)
                                                 .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                 .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(UNIVERSITY))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
