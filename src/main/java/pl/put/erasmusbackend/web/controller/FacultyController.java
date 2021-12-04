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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.dto.FacultyRequestDto;
import pl.put.erasmusbackend.dto.FacultyResponseDto;
import pl.put.erasmusbackend.service.FacultyService;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static pl.put.erasmusbackend.web.common.CommonRequestVariable.*;
import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;
import static pl.put.erasmusbackend.web.controller.ControllerUtils.withPageParams;
import static pl.put.erasmusbackend.web.controller.ControllerUtils.withPathParam;

@RestController
@AllArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;

    @NonNull
    @Operation(
            operationId = "list-faculties",
            tags = "Faculty",
            description = "List faculties",
            summary = "List Faculties by university ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = FacultyResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listFaculties(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPageParams(request,
                        pageRequest -> facultyService.listEntities(universityId, pageRequest)
                                                     .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "create-faculty",
            tags = "Faculty",
            description = "Create faculty",
            summary = "Create faculty",
            parameters = @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FacultyRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = FacultyResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createFaculty(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(FacultyRequestDto.class)
                                       .flatMap(faculty -> facultyService.createEntity(faculty, universityId))
                                       .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                       .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "get-faculty",
            tags = "Faculty",
            description = "Get faculty by ID",
            summary = "Get faculty by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = FacultyResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getFacultyById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> facultyService.getEntityById(facultyId, universityId)
                                                   .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                   .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                                   .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "update-faculty",
            tags = "Faculty",
            description = "Update faculty",
            summary = "Update faculty",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = FacultyRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = FacultyResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateFaculty(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> request.bodyToMono(FacultyRequestDto.class)
                                            .flatMap(facultyDto -> facultyService.updateEntity(facultyDto, facultyId, universityId))
                                            .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                            .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                            .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                            .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                            .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "delete-faculty",
            tags = "Faculty",
            description = "Delete faculty",
            summary = "Delete faculty by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deleteFaculty(ServerRequest request) {
        return withPathParam(request, BUILDING_PATH_PARAM,
                facultyId -> facultyService.deleteEntity(facultyId)
                                           .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
