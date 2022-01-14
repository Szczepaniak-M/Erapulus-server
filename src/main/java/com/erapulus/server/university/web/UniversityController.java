package com.erapulus.server.university.web;

import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.student.dto.StudentResponseDto;
import com.erapulus.server.university.dto.UniversityListDto;
import com.erapulus.server.university.dto.UniversityRequestDto;
import com.erapulus.server.university.dto.UniversityResponseDto;
import com.erapulus.server.university.service.UniversityService;
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
import org.springframework.dao.DataIntegrityViolationException;
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

import static com.erapulus.server.common.web.CommonRequestVariable.FILE_QUERY_PARAM;
import static com.erapulus.server.common.web.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.common.web.ControllerUtils.withPathParam;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;


@Slf4j
@RestController
@AllArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @NonNull
    @Operation(
            operationId = "list-universities",
            tags = "University",
            description = "List universities",
            summary = "List universities",
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = UniversityListDto.class)))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listUniversities(ServerRequest request) {
        return universityService.listUniversities()
                                .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                .doOnError(e -> log.error(e.getMessage(), e))
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
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createUniversity(ServerRequest request) {
        return request.bodyToMono(UniversityRequestDto.class)
                      .flatMap(universityService::createUniversity)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("university"))
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "get-university",
            tags = "University",
            description = "Get university by ID",
            summary = "Get university by ID",
            parameters = @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getUniversityById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> universityService.getUniversityById(universityId)
                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                 .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                 .doOnError(e -> log.error(e.getMessage(), e))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-university",
            tags = "University",
            description = "Update university",
            summary = "Update university",
            parameters = @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UniversityRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(UniversityRequestDto.class)
                                       .flatMap(universityDto -> universityService.updateUniversity(universityDto, universityId))
                                       .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                       .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                       .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                       .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("university"))
                                       .doOnError(e -> log.error(e.getMessage(), e))
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "delete-university",
            tags = "University",
            description = "Delete university",
            summary = "Delete university by ID",
            parameters = @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "204", description = NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> universityService.deleteUniversity(universityId)
                                                 .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                 .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                 .doOnError(e -> log.error(e.getMessage(), e))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-university-logo",
            tags = "University",
            summary = "Update university's photo",
            description = "Update university's photo",
            requestBody = @RequestBody(content = @Content(schema = @Schema(type = "file")), required = true),
            parameters = @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = UniversityResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateUniversityPhoto(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> request.body(BodyExtractors.toMultipartData())
                                       .flatMap(this::extractPhoto)
                                       .flatMap(photo -> universityService.updateUniversityLogo(universityId, photo))
                                       .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                       .onErrorResume(DecodingException.class, e -> ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())
                                       .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                       .doOnError(e -> log.error(e.getMessage(), e))
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    private Mono<FilePart> extractPhoto(MultiValueMap<String, Part> photoParts) {
        FilePart filePart = (FilePart) photoParts.toSingleValueMap().get(FILE_QUERY_PARAM);
        return Mono.justOrEmpty(filePart);
    }
}
