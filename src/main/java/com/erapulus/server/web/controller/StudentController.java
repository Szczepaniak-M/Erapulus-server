package com.erapulus.server.web.controller;

import com.erapulus.server.dto.StudentRequestDto;
import com.erapulus.server.dto.StudentResponseDto;
import com.erapulus.server.service.StudentService;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static com.erapulus.server.web.common.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static com.erapulus.server.web.controller.ControllerUtils.withPathParam;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@AllArgsConstructor
public class StudentController {

    private static final String STUDENT = "student";
    private final StudentService studentService;

    @NonNull
    @Operation(
            operationId = "get-student",
            tags = "Student",
            description = "Get student by ID",
            summary = "Get student by ID",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getStudentById(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> studentService.getEntityById(studentId)
                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(STUDENT))
                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-student",
            tags = "Student",
            description = "Update student",
            summary = "Update student",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StudentRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateStudent(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> request.bodyToMono(StudentRequestDto.class)
                                    .flatMap(studentDto -> studentService.updateEntity(studentDto, studentId))
                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                    .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(STUDENT))
                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "delete-student",
            tags = "Student",
            description = "Delete student by ID",
            summary = "Delete student",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteStudent(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> studentService.deleteEntity(studentId)
                                           .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse(STUDENT))
                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}

