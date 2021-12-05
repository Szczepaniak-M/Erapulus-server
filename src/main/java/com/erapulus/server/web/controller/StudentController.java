package com.erapulus.server.web.controller;

import com.erapulus.server.web.common.OpenApiConstants;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.erapulus.server.dto.StudentDto;
import com.erapulus.server.service.StudentService;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
public class StudentController {

    private StudentService studentService;

    @Operation(
            operationId = "get-student",
            tags = "Student",
            description = "Get student",
            summary = "Get student",
            parameters = @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "200", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = StudentDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "404", description = OpenApiConstants.CONFLICT),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getStudent(ServerRequest request) {
        return Mono.just(request.pathVariable("id"))
                   .map(Integer::parseInt)
                   .flatMap(studentService::getStudent)
                   .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                   .onErrorResume(NumberFormatException.class, ServerResponseFactory::createHttpBadRequestCantParseToIntegerErrorResponse)
                   .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                   .switchIfEmpty(ServerResponseFactory.createHttpNotFoundResponse("student"));
    }
}

