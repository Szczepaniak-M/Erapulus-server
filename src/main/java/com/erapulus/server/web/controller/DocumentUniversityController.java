package com.erapulus.server.web.controller;

import com.erapulus.server.dto.DocumentRequestDto;
import com.erapulus.server.dto.DocumentResponseDto;
import com.erapulus.server.service.DocumentService;
import com.erapulus.server.web.common.ServerResponseFactory;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static com.erapulus.server.web.common.CommonRequestVariable.DOCUMENT_PATH_PARAM;
import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static com.erapulus.server.web.controller.ControllerUtils.withPathParam;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Slf4j
@RestController
@AllArgsConstructor
public class DocumentUniversityController {

    private final DocumentService documentService;

    @NonNull
    @Operation(
            operationId = "list-documents-for-university",
            tags = "Document",
            summary = "List documents for university",
            description = "List documents for university",
            parameters = @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listDocumentsForUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> documentService.listEntities(universityId, null, null, null)
                                               .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                               .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                               .doOnError(e -> log.error(e.getMessage(), e))
                                               .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "upload-document-for-university",
            tags = "Document",
            summary = "Upload document for university",
            description = "Upload document for university",
            requestBody = @RequestBody(content = @Content(schema = @Schema(type = "file")), required = true),
            parameters = @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = DocumentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> uploadDocumentForUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> request.body(BodyExtractors.toMultipartData())
                                       .map(MultiValueMap::toSingleValueMap)
                                       .flatMap(body -> documentService.createEntity(universityId, null, null, null, body))
                                       .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                       .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                       .doOnError(e -> log.error(e.getMessage(), e))
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "get-document-by-id-for-university",
            tags = "Document",
            summary = "Get document by ID for university",
            description = "Get document by ID for university",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
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
    public Mono<ServerResponse> getDocumentForUniversityById(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, DOCUMENT_PATH_PARAM,
                        documentId -> documentService.getEntityById(documentId, universityId, null, null, null)
                                                     .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "update-document-for-university",
            tags = "Document",
            summary = "Update document for university",
            description = "Update document for university",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = DocumentRequestDto.class)), required = true),
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
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
    public Mono<ServerResponse> updateDocumentForUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, DOCUMENT_PATH_PARAM,
                        documentId -> request.bodyToMono(DocumentRequestDto.class)
                                             .flatMap(document -> documentService.updateEntity(document, documentId, universityId, null, null, null))
                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                             .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                             .doOnError(e -> log.error(e.getMessage(), e))
                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                             .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "delete-document-for-university",
            tags = "Document",
            summary = "Delete document for university",
            description = "Delete document for university",
            parameters = {
                    @Parameter(in = PATH, name = UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
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
    public Mono<ServerResponse> deleteDocumentForUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> withPathParam(request, DOCUMENT_PATH_PARAM,
                        documentId -> documentService.deleteEntity(documentId, universityId, null, null, null)
                                                     .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                     .doOnError(e -> log.error(e.getMessage(), e))
                                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }
}
