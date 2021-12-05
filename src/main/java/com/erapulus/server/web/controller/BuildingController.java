package com.erapulus.server.web.controller;

import com.erapulus.server.dto.BuildingRequestDto;
import com.erapulus.server.dto.BuildingResponseDto;
import com.erapulus.server.service.BuildingService;
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
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @NonNull
    @Operation(
            operationId = "list-buildings",
            tags = "Building",
            description = "List buildings",
            summary = "List building by university ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = BuildingResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listBuildings(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> buildingService.listEntities(universityId)
                                               .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                               .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "create-building",
            tags = "Building",
            description = "Create building",
            summary = "Create building",
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BuildingRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = BuildingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createBuilding(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(BuildingRequestDto.class)
                                       .flatMap(building -> buildingService.createEntity(building, universityId))
                                       .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                       .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-building",
            tags = "Building",
            description = "Update building",
            summary = "Update building",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.BUILDING_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = BuildingRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = BuildingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateBuilding(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.BUILDING_PATH_PARAM,
                        buildingId -> request.bodyToMono(BuildingRequestDto.class)
                                             .flatMap(buildingDto -> buildingService.updateEntity(buildingDto, buildingId, universityId))
                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                             .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                             .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                             .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "delete-building",
            tags = "Building",
            description = "Delete building",
            summary = "Delete building by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.BUILDING_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = OpenApiConstants.NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteBuilding(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.BUILDING_PATH_PARAM,
                buildingId -> buildingService.deleteEntity(buildingId)
                                             .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                             .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
