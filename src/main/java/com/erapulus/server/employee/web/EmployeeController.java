package com.erapulus.server.employee.web;

import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.employee.dto.EmployeeRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import com.erapulus.server.employee.service.EmployeeService;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.erapulus.server.common.web.CommonRequestVariable.EMPLOYEE_PATH_PARAM;
import static com.erapulus.server.common.web.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.common.web.ControllerUtils.withPathParam;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Slf4j
@RestController
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @NonNull
    @Operation(
            operationId = "list-employee",
            tags = {"Employee", "University"},
            description = "List employees from university",
            summary = "List employees",
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listEmployeeFromUniversity(ServerRequest request) {
        return withPathParam(request, UNIVERSITY_PATH_PARAM,
                universityId -> employeeService.listEmployees(universityId)
                                               .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                               .onErrorResume(AccessDeniedException.class, e -> ServerResponseFactory.createHttpForbiddenErrorResponse())
                                               .doOnError(e -> log.error(e.getMessage(), e))
                                               .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "get-employee",
            tags = "Employee",
            description = "Get employee by ID",
            summary = "Get employee by ID",
            parameters = @Parameter(in = PATH, name = EMPLOYEE_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getEmployeeById(ServerRequest request) {
        return withPathParam(request, EMPLOYEE_PATH_PARAM,
                employeeId -> employeeService.getEmployeeById(employeeId)
                                             .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                             .onErrorResume(AccessDeniedException.class, e -> ServerResponseFactory.createHttpForbiddenErrorResponse())
                                             .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                             .doOnError(e -> log.error(e.getMessage(), e))
                                             .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "update-employee",
            tags = "Employee",
            description = "Update employee",
            summary = "Update employee",
            parameters = @Parameter(in = PATH, name = EMPLOYEE_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = EmployeeRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = EmployeeResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateEmployee(ServerRequest request) {
        return withPathParam(request, EMPLOYEE_PATH_PARAM,
                employeeId -> request.bodyToMono(EmployeeRequestDto.class)
                                     .flatMap(employeeDto -> employeeService.updateEmployee(employeeDto, employeeId))
                                     .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                     .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                     .onErrorResume(AccessDeniedException.class, e -> ServerResponseFactory.createHttpForbiddenErrorResponse())
                                     .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                     .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("employee"))
                                     .doOnError(e -> log.error(e.getMessage(), e))
                                     .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                     .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }
}
