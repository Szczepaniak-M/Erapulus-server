package com.erapulus.server.web.controller;

import com.erapulus.server.dto.FacultyResponseDto;
import com.erapulus.server.dto.ModuleRequestDto;
import com.erapulus.server.dto.ModuleResponseDto;
import com.erapulus.server.service.ModuleService;
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
public class ModuleController {

    private final ModuleService moduleService;

    @NonNull
    @Operation(
            operationId = "list-modules",
            tags = "Module",
            description = "List modules",
            summary = "List modules by university ID, faculty ID and program ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = FacultyResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listModules(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                programId -> ControllerUtils.withPageParams(request,
                                        pageRequest -> moduleService.listEntities(universityId, facultyId, programId, pageRequest)
                                                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "create-module",
            tags = "Module",
            description = "Create module",
            summary = "Create module",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ModuleRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = ModuleResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createModule(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                programId -> request.bodyToMono(ModuleRequestDto.class)
                                                    .flatMap(module -> moduleService.createEntity(module, universityId, facultyId, programId))
                                                    .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "get-module",
            tags = "Module",
            description = "Get module by ID",
            summary = "Get module by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.MODULE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = ModuleResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getModuleById(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                programId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                        moduleId -> moduleService.getEntityById(moduleId, universityId, facultyId, programId)
                                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                 .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "update-module",
            tags = "Module",
            description = "Update module",
            summary = "Update module",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.MODULE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ModuleRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = ModuleResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateModule(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                programId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                        moduleId -> request.bodyToMono(ModuleRequestDto.class)
                                                           .flatMap(module -> moduleService.updateEntity(module, moduleId, universityId, facultyId, programId))
                                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                           .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                                           .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "delete-module",
            tags = "Module",
            description = "Delete module",
            summary = "Delete module by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.MODULE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deleteModule(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.MODULE_PATH_PARAM,
                moduleId -> moduleService.deleteEntity(moduleId)
                                         .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                         .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                         .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
