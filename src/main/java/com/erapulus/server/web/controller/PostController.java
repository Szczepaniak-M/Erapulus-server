package com.erapulus.server.web.controller;

import com.erapulus.server.service.PostService;
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
import com.erapulus.server.dto.PostRequestDto;
import com.erapulus.server.dto.PostResponseDto;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @NonNull
    @Operation(
            operationId = "list-post",
            tags = "Post",
            description = "List post",
            summary = "List post by university ID with filtering by date and title",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.QUERY, name = CommonRequestVariable.TITLE_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = ParameterIn.QUERY, name = CommonRequestVariable.FROM_QUERY_PARAM, schema = @Schema(type = "date")),
                    @Parameter(in = ParameterIn.QUERY, name = CommonRequestVariable.TO_QUERY_PARAM, schema = @Schema(type = "date")),
                    @Parameter(in = ParameterIn.QUERY, name = CommonRequestVariable.PAGE_QUERY_PARAM, schema = @Schema(type = "integer")),
                    @Parameter(in = ParameterIn.QUERY, name = CommonRequestVariable.PAGE_SIZE_QUERY_PARAM, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listPosts(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withQueryParam(request, CommonRequestVariable.TITLE_QUERY_PARAM,
                        title -> ControllerUtils.withQueryParam(request, CommonRequestVariable.FROM_QUERY_PARAM,
                                fromDate -> ControllerUtils.withQueryParam(request, CommonRequestVariable.TO_QUERY_PARAM,
                                        toDate -> ControllerUtils.withPageParams(request,
                                                pageRequest -> postService.listEntities(universityId, title, fromDate, toDate, pageRequest)
                                                                          .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                                          .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()))))));
    }

    @NonNull
    @Operation(
            operationId = "create-post",
            tags = "Post",
            description = "Create post",
            summary = "Create post",
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = PostRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createPost(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> request.bodyToMono(PostRequestDto.class)
                                       .flatMap(post -> postService.createEntity(post, universityId))
                                       .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                       .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                       .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                       .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "get-post",
            tags = "Post",
            description = "Get post by ID",
            summary = "Get post by ID",
            parameters = @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getPostById(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.POST_PATH_PARAM,
                        postId -> postService.getEntityById(postId, universityId)
                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                             .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "update-post",
            tags = "Post",
            description = "Update post",
            summary = "Update post",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.POST_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = PostRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = OpenApiConstants.UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = OpenApiConstants.FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updatePost(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.UNIVERSITY_PATH_PARAM,
                universityId -> ControllerUtils.withPathParam(request, CommonRequestVariable.POST_PATH_PARAM,
                        postId -> request.bodyToMono(PostRequestDto.class)
                                         .flatMap(postDto -> postService.updateEntity(postDto, postId, universityId))
                                         .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                         .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                         .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                         .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                         .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "delete-post",
            tags = "Post",
            description = "Delete post",
            summary = "Delete post by ID",
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.UNIVERSITY_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = ParameterIn.PATH, name = CommonRequestVariable.POST_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
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
    public Mono<ServerResponse> deletePost(ServerRequest request) {
        return ControllerUtils.withPathParam(request, CommonRequestVariable.POST_PATH_PARAM,
                postId -> postService.deleteEntity(postId)
                                     .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                     .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}
