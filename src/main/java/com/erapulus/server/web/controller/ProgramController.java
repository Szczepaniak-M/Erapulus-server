package com.erapulus.server.web.controller;

import com.erapulus.server.dto.ProgramRequestDto;
import com.erapulus.server.dto.ProgramResponseDto;
import com.erapulus.server.service.ProgramService;
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
public class ProgramController {

    private final ProgramService programService;

    @NonNull
    @Operation(
            operationId = "list-programs",
            tags = "Program",
            description = "List programs",
            summary = "List programs by university ID and faculty ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProgramResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listPrograms(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPageParams(request,
                                pageRequest -> programService.listEntities(universityId, facultyId, pageRequest)
                                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "create-program",
            tags = "Program",
            description = "Create program",
            summary = "Create program",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProgramRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = ProgramResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createProgram(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> request.bodyToMono(ProgramRequestDto.class)
                                            .flatMap(program -> programService.createEntity(program, universityId, facultyId))
                                            .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                            .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                            .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                            .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "get-program",
            tags = "Program",
            description = "Get program by ID",
            summary = "Get program by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = ProgramResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getProgramById(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                programId -> programService.getEntityById(programId, universityId, facultyId)
                                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "update-program",
            tags = "Program",
            description = "Update program",
            summary = "Update program",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProgramRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = ProgramResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateProgram(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.FACULTY_PATH_PARAM,
                        facultyId -> ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                                programId -> request.bodyToMono(ProgramRequestDto.class)
                                                    .flatMap(program -> programService.updateEntity(program, programId, universityId, facultyId))
                                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                    .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "delete-program",
            tags = "Program",
            description = "Delete program",
            summary = "Delete program by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deleteProgram(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.PROGRAM_PATH_PARAM,
                programId -> programService.deleteEntity(programId)
                                           .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}

