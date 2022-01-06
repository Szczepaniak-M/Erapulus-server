package com.erapulus.server.applicationuser.web;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.applicationuser.service.RegisterService;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.employee.dto.EmployeeCreateRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

import static com.erapulus.server.common.web.OpenApiConstants.*;
import static com.erapulus.server.security.SecurityContextUtils.withSecurityContext;

@Slf4j
@RestController
@AllArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @NonNull
    @Operation(
            operationId = "create-administrator",
            tags = "Employee",
            description = "Create administrator",
            summary = "Create administrator",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = EmployeeCreateRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createAdministrator(ServerRequest request) {
        return request.bodyToMono(EmployeeCreateRequestDto.class)
                      .flatMap(registerService::createAdministrator)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("administrator"))
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "create-university-administrator",
            tags = "Employee",
            description = "Create university administrator",
            summary = "Create university administrator",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = EmployeeCreateRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createUniversityAdministrator(ServerRequest request) {
        return request.bodyToMono(EmployeeCreateRequestDto.class)
                      .flatMap(this::validateBodyContent)
                      .flatMap(registerService::createUniversityAdministrator)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(AccessDeniedException.class, e -> ServerResponseFactory.createHttpForbiddenErrorResponse())
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("universityAdministrator"))
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    @NonNull
    @Operation(
            operationId = "create-employee",
            tags = "Employee",
            description = "Create university employee",
            summary = "Create university employee",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = EmployeeCreateRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createUniversityEmployee(ServerRequest request) {
        return request.bodyToMono(EmployeeCreateRequestDto.class)
                      .flatMap(this::validateBodyContent)
                      .flatMap(registerService::createUniversityEmployee)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(AccessDeniedException.class, e -> ServerResponseFactory.createHttpForbiddenErrorResponse())
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("employee"))
                      .doOnError(e -> log.error(e.getMessage(), e))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }

    private Mono<EmployeeCreateRequestDto> validateBodyContent(EmployeeCreateRequestDto employeeCreateRequestDto) {
        return withSecurityContext(user -> validateBodyContent(employeeCreateRequestDto, user))
                .switchIfEmpty(Mono.just(employeeCreateRequestDto));
    }

    private Mono<EmployeeCreateRequestDto> validateBodyContent(EmployeeCreateRequestDto employeeCreateRequestDto, ApplicationUserEntity user) {
        if (user.type() == UserType.UNIVERSITY_ADMINISTRATOR && !Objects.equals(user.universityId(), employeeCreateRequestDto.universityId())) {
            return Mono.error(new AccessDeniedException("access.denied"));
        }
        return Mono.just(employeeCreateRequestDto);
    }
}
