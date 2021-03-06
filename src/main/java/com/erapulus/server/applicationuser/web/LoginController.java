package com.erapulus.server.applicationuser.web;

import com.erapulus.server.applicationuser.dto.LoginResponseDto;
import com.erapulus.server.applicationuser.service.LoginService;
import com.erapulus.server.common.exception.InvalidTokenException;
import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.applicationuser.dto.EmployeeLoginDto;
import com.erapulus.server.applicationuser.dto.StudentLoginDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

import static com.erapulus.server.common.web.OpenApiConstants.*;

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
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = EmployeeLoginDto.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> loginEmployee(ServerRequest request) {
        return request.bodyToMono(EmployeeLoginDto.class)
                      .flatMap(loginService::validateEmployeeCredentials)
                      .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(BadCredentialsException.class, e -> ServerResponseFactory.createHttpBadRequestInvalidCredentialsErrorResponse())
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "login-student-google",
            tags = "Login",
            description = "Login student using Google",
            summary = "Login student using Google",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StudentLoginDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = CREATED, content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> loginStudentGoogle(ServerRequest request) {
        return request.bodyToMono(StudentLoginDto.class)
                      .flatMap(loginService::validateGoogleStudentCredentials)
                      .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(InvalidTokenException.class, e -> ServerResponseFactory.createHttpBadRequestInvalidCredentialsErrorResponse())
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "login-student-facebook",
            tags = "Login",
            description = "Login student using Facebook",
            summary = "Login student using Facebook",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = StudentLoginDto.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = CREATED, content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> loginStudentFacebook(ServerRequest request) {
        return request.bodyToMono(StudentLoginDto.class)
                      .flatMap(loginService::validateFacebookStudentCredentials)
                      .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(InvalidTokenException.class, e -> ServerResponseFactory.createHttpBadRequestInvalidCredentialsErrorResponse())
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }
}

