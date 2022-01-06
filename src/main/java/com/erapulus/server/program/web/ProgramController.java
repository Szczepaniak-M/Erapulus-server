package com.erapulus.server.program.web;

import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.program.dto.ProgramRequestDto;
import com.erapulus.server.program.dto.ProgramResponseDto;
import com.erapulus.server.program.service.ProgramService;
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
public class ProgramController {

    private final ProgramService programService;

    @NonNull
    @Operation(
            operationId = "list-programs",
            tags = "Program",
            description = "List programs",
            summary = "List programs by university ID and faculty ID",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = QUERY, name = NAME_QUERY_PARAM, schema = @Schema(type = "string")),
                    @Parameter(in = QUERY, name = PAGE_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = QUERY, name = PAGE_SIZE_QUERY_PARAM, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProgramResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listPrograms(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withQueryParam(request, NAME_QUERY_PARAM,
                                name -> withPageParams(request,
                                        pageRequest -> programService.listPrograms(universityId, facultyId, name, pageRequest)
                                                                     .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "create-program",
            tags = "Program",
            description = "Create program",
            summary = "Create program",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProgramRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = ProgramResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> request.bodyToMono(ProgramRequestDto.class)
                                            .flatMap(program -> programService.createProgram(program, universityId, facultyId))
                                            .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                            .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                            .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                            .doOnError(e -> log.error(e.getMessage(), e))
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
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = ProgramResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getProgramById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> programService.getProgramById(programId, universityId, facultyId)
                                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                           .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                           .doOnError(e -> log.error(e.getMessage(), e))
                                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "update-program",
            tags = "Program",
            description = "Update program",
            summary = "Update program",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ProgramRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = ProgramResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> request.bodyToMono(ProgramRequestDto.class)
                                                    .flatMap(program -> programService.updateProgram(program, programId, universityId, facultyId))
                                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                    .doOnError(e -> log.error(e.getMessage(), e))
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
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deleteProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> programService.deleteProgram(programId, universityId, facultyId)
                                                           .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                           .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                           .doOnError(e -> log.error(e.getMessage(), e))
                                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))));
    }
}

