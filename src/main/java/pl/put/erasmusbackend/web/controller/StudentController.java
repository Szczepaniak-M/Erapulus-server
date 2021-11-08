package pl.put.erasmusbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.dto.StudentDto;
import pl.put.erasmusbackend.service.StudentService;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;

@Controller
@AllArgsConstructor
public class StudentController {

    private StudentService studentService;

    @Operation(
            operationId = "get-student",
            tags = "Student",
            description = "Get student",
            summary = "Get student",
            parameters = @Parameter (in = ParameterIn.PATH, name ="id" ,schema = @Schema(type = "integer")),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = StudentDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "404", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
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

