package pl.put.erasmusbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.dto.EmployeeLoginDTO;
import pl.put.erasmusbackend.dto.LoginResponseDTO;
import pl.put.erasmusbackend.dto.StudentLoginDTO;
import pl.put.erasmusbackend.service.LoginService;
import pl.put.erasmusbackend.service.exception.InvalidPasswordException;
import pl.put.erasmusbackend.service.exception.NoSuchUserException;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;

@Controller
@AllArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @NonNull
    @Operation(
            operationId = "login-employee",
            tags = "Login",
            description = "Login employee",
            summary = "Login employee",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = EmployeeLoginDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> loginEmployee(ServerRequest request) {
        return request.bodyToMono(EmployeeLoginDTO.class)
                      .flatMap(loginService::validateEmployeeCredentials)
                      .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(NoSuchUserException.class, e -> ServerResponseFactory.createHttpUnauthorizedResponse())
                      .onErrorResume(InvalidPasswordException.class, e -> ServerResponseFactory.createHttpUnauthorizedResponse())
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "login-student-google",
            tags = "Login",
            description = "Login student using Google",
            summary = "Login student using Google",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StudentLoginDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = CREATED, content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> loginStudentGoogle(ServerRequest request) {
        return request.bodyToMono(StudentLoginDTO.class)
                      .flatMap(loginService::validateGoogleStudentCredentials)
                      .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse());
    }
}

