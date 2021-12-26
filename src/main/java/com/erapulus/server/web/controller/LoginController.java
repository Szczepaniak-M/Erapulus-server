package com.erapulus.server.web.controller;

import com.erapulus.server.dto.EmployeeLoginDTO;
import com.erapulus.server.dto.LoginResponseDTO;
import com.erapulus.server.dto.StudentLoginDTO;
import com.erapulus.server.service.LoginService;
import com.erapulus.server.service.exception.InvalidPasswordException;
import com.erapulus.server.service.exception.InvalidTokenException;
import com.erapulus.server.service.exception.NoSuchUserException;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

import static com.erapulus.server.web.common.OpenApiConstants.*;

@Slf4j
@RestController
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
                      .onErrorResume(NoSuchUserException.class, e -> ServerResponseFactory.createHttpBadRequestInvalidCredentialsErrorResponse())
                      .onErrorResume(InvalidPasswordException.class, e -> ServerResponseFactory.createHttpUnauthorizedResponse())
                      .doOnError(e -> log.error(e.getMessage(), e))
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
                      .onErrorResume(InvalidTokenException.class, e -> ServerResponseFactory.createHttpBadRequestInvalidCredentialsErrorResponse())
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse());
    }
}

