package pl.put.erasmusbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.dto.BuildingRequestDto;
import pl.put.erasmusbackend.dto.BuildingResponseDto;
import pl.put.erasmusbackend.service.BuildingService;
import pl.put.erasmusbackend.service.exception.NoSuchBuildingException;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

import static pl.put.erasmusbackend.web.common.CommonPathVariable.BUILDING_PATH_PARAM;
import static pl.put.erasmusbackend.web.common.CommonPathVariable.UNIVERSITY_PATH_PARAM;
import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;
import static pl.put.erasmusbackend.web.controller.ControllerUtils.withPathParam;

@Controller
@AllArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @NonNull
    @Operation(
            operationId = "list-buildings",
            tags = "Building",
            description = "List buildings",
            summary = "List building by university ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = BuildingResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listBuildingByUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> buildingService.listBuildingByUniversityId(universityId)
                                               .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                               .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                               .onErrorResume(NumberFormatException.class, ServerResponseFactory::createHttpBadRequestCantParseToIntegerErrorResponse)
                                               .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "create-building",
            tags = "Building",
            description = "Create building",
            summary = "Create building",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = BuildingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createBuilding(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(BuildingRequestDto.class)
                                       .flatMap(building -> buildingService.createBuilding(universityId, building))
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
                    @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = ParameterIn.PATH, name = BUILDING_PATH_PARAM, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = BuildingResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateBuilding(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, BUILDING_PATH_PARAM,
                        buildingId -> request.bodyToMono(BuildingRequestDto.class)
                                             .flatMap(buildingDto -> buildingService.updateBuilding(universityId, buildingId, buildingDto))
                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                             .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                             .onErrorResume(NoSuchBuildingException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
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
                    @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = ParameterIn.PATH, name = BUILDING_PATH_PARAM, schema = @Schema(type = "integer"))
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
    public Mono<ServerResponse> deleteBuilding(ServerRequest request) {
        return withPathParam(request, BUILDING_PATH_PARAM,
                buildingId -> buildingService.deleteBuilding(buildingId)
                                             .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                             .onErrorResume(NumberFormatException.class, ServerResponseFactory::createHttpBadRequestCantParseToIntegerErrorResponse)
                                             .onErrorResume(NoSuchBuildingException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
