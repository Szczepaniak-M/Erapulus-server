package com.erapulus.server.student.web;

import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.student.dto.StudentListDto;
import com.erapulus.server.student.dto.StudentRequestDto;
import com.erapulus.server.student.dto.StudentResponseDto;
import com.erapulus.server.student.dto.StudentUniversityUpdateDto;
import com.erapulus.server.student.service.StudentService;
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
import org.springframework.core.codec.DecodingException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.erapulus.server.common.web.CommonRequestVariable.*;
import static com.erapulus.server.common.web.ControllerUtils.withPathParam;
import static com.erapulus.server.common.web.ControllerUtils.withQueryParam;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Slf4j
@RestController
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @NonNull
    @Operation(
            operationId = "list-students",
            tags = "Student",
            description = "List students",
            summary = "List students",
            parameters = @Parameter(in = QUERY, name = NAME_QUERY_PARAM, schema = @Schema(type = "string")),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentListDto.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listStudents(ServerRequest request) {
        return withQueryParam(request, NAME_QUERY_PARAM,
                name -> studentService.listStudents(name)
                                      .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                      .doOnError(e -> log.error(e.getMessage(), e))
                                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "get-student",
            tags = "Student",
            description = "Get student by ID",
            summary = "Get student by ID",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getStudentById(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> studentService.getStudentById(studentId)
                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                           .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                           .doOnError(e -> log.error(e.getMessage(), e))
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
                                    .flatMap(studentDto -> studentService.updateStudent(studentDto, studentId))
                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                    .doOnError(e -> log.error(e.getMessage(), e))
                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-student-university",
            tags = "Student",
            summary = "Update student's universityId",
            description = "Update student's universityId",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StudentUniversityUpdateDto.class)), required = true),
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentUniversityUpdateDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateStudentUniversity(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> request.bodyToMono(StudentUniversityUpdateDto.class)
                                    .flatMap(universityDto -> studentService.updateStudentUniversity(universityDto, studentId))
                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                    .doOnError(e -> log.error(e.getMessage(), e))
                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-student-photo",
            tags = "Student",
            summary = "Update student's photo",
            description = "Update student's photo",
            requestBody = @RequestBody(content = @Content(schema = @Schema(type = "file")), required = true),
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = StudentResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateStudentPhoto(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> request.body(BodyExtractors.toMultipartData())
                                    .flatMap(this::extractPhoto)
                                    .flatMap(photo -> studentService.updateStudentPhoto(studentId, photo))
                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                    .onErrorResume(DecodingException.class, e -> ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())
                                    .doOnError(e -> log.error(e.getMessage(), e))
                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    private Mono<FilePart> extractPhoto(MultiValueMap<String, Part> photoParts) {
        FilePart filePart = (FilePart) photoParts.toSingleValueMap().get(FILE_QUERY_PARAM);
        return Mono.justOrEmpty(filePart);
    }
}

