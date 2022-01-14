package com.erapulus.server.module.web;

import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.faculty.dto.FacultyResponseDto;
import com.erapulus.server.module.dto.ModuleRequestDto;
import com.erapulus.server.module.dto.ModuleResponseDto;
import com.erapulus.server.module.service.ModuleService;
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

import static com.erapulus.server.common.web.CommonRequestVariable.*;
import static com.erapulus.server.common.web.ControllerUtils.*;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Slf4j
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
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = QUERY, name = NAME_QUERY_PARAM, schema = @Schema(type = "string")),
                    @Parameter(in = QUERY, name = PAGE_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = QUERY, name = PAGE_SIZE_QUERY_PARAM, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = FacultyResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listModules(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withQueryParam(request, NAME_QUERY_PARAM,
                                        name -> withPageParams(request,
                                                pageRequest -> moduleService.listModules(universityId, facultyId, programId, name, pageRequest)
                                                                            .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                            .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                            .doOnError(e -> log.error(e.getMessage(), e))
                                                                            .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))))));
    }

    @NonNull
    @Operation(
            operationId = "create-module",
            tags = "Module",
            description = "Create module",
            summary = "Create module",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ModuleRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = ModuleResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createModule(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> request.bodyToMono(ModuleRequestDto.class)
                                                    .flatMap(module -> moduleService.createModule(module, universityId, facultyId, programId))
                                                    .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                    .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("module"))
                                                    .doOnError(e -> log.error(e.getMessage(), e))
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
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = MODULE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = ModuleResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getModuleById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withPathParam(request, MODULE_PATH_PARAM,
                                        moduleId -> moduleService.getModuleById(moduleId, universityId, facultyId, programId)
                                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                 .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                 .doOnError(e -> log.error(e.getMessage(), e))
                                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "update-module",
            tags = "Module",
            description = "Update module",
            summary = "Update module",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = MODULE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ModuleRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = ModuleResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateModule(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withPathParam(request, MODULE_PATH_PARAM,
                                        moduleId -> request.bodyToMono(ModuleRequestDto.class)
                                                           .flatMap(module -> moduleService.updateModule(module, moduleId, universityId, facultyId, programId))
                                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                           .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                           .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                           .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("module"))
                                                           .doOnError(e -> log.error(e.getMessage(), e))
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
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = MODULE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deleteModule(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withPathParam(request, MODULE_PATH_PARAM,
                                        moduleId -> moduleService.deleteModule(moduleId, universityId, facultyId, programId)
                                                                 .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                                 .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                 .doOnError(e -> log.error(e.getMessage(), e))
                                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }
}
