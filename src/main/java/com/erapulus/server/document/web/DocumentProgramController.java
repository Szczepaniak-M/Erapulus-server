package com.erapulus.server.document.web;

import com.erapulus.server.document.dto.DocumentRequestDto;
import com.erapulus.server.document.dto.DocumentResponseDto;
import com.erapulus.server.document.service.DocumentService;
import com.erapulus.server.common.web.ServerResponseFactory;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.erapulus.server.common.web.CommonRequestVariable.*;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static com.erapulus.server.common.web.ControllerUtils.withPathParam;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Slf4j
@RestController
@AllArgsConstructor
public class DocumentProgramController {

    private final DocumentService documentService;

    @NonNull
    @Operation(
            operationId = "list-documents-for-program",
            tags = "Document",
            summary = "List documents for program",
            description = "List documents for program",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listDocumentsForProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> documentService.listDocuments(universityId, facultyId, programId, null)
                                                            .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                            .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                            .doOnError(e -> log.error(e.getMessage(), e))
                                                            .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "upload-document-for-program",
            tags = "Document",
            summary = "Upload document for program",
            description = "Upload document for program",
            requestBody = @RequestBody(content = @Content(schema = @Schema(type = "file")), required = true),
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = DocumentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> uploadDocumentForProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> request.body(BodyExtractors.toMultipartData())
                                                    .map(MultiValueMap::toSingleValueMap)
                                                    .flatMap(body -> documentService.createDocument(universityId, facultyId, programId, null, body))
                                                    .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                    .onErrorResume(DecodingException.class, e -> ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())
                                                    .onErrorResume(IllegalArgumentException.class, e -> ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())
                                                    .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                    .doOnError(e -> log.error(e.getMessage(), e))
                                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()))));
    }

    @NonNull
    @Operation(
            operationId = "get-document-by-id-for-program",
            tags = "Document",
            summary = "Get document by ID for program",
            description = "Get document by ID for program",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = DOCUMENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = DocumentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getDocumentForProgramById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withPathParam(request, DOCUMENT_PATH_PARAM,
                                        documentId -> documentService.getDocumentById(documentId, universityId, facultyId, programId, null)
                                                                     .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "update-document-for-program",
            tags = "Document",
            summary = "Update document for program",
            description = "Update document for program",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = DocumentRequestDto.class)), required = true),
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = DOCUMENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = DocumentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateDocumentForProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withPathParam(request, DOCUMENT_PATH_PARAM,
                                        documentId -> request.bodyToMono(DocumentRequestDto.class)
                                                             .flatMap(document -> documentService.updateDocument(document, documentId, universityId, facultyId, programId, null))
                                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                             .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                                             .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                             .doOnError(e -> log.error(e.getMessage(), e))
                                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                                             .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())))));
    }

    @NonNull
    @Operation(
            operationId = "delete-document-for-program",
            tags = "Document",
            summary = "Delete document for program",
            description = "Delete document for program",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = FACULTY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = PROGRAM_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = DOCUMENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deleteDocumentForProgram(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, FACULTY_PATH_PARAM,
                        facultyId -> withPathParam(request, PROGRAM_PATH_PARAM,
                                programId -> withPathParam(request, DOCUMENT_PATH_PARAM,
                                        documentId -> documentService.deleteDocument(documentId, universityId, facultyId, programId, null)
                                                                     .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())))));
    }
}
