package pl.put.erasmusbackend.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;
import pl.put.erasmusbackend.service.EmployeeService;
import pl.put.erasmusbackend.web.common.ServerResponseFactory;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;

@Controller
@AllArgsConstructor
public class EmployeeController {

    private EmployeeService employeeService;

    @Operation(
            operationId = "create-employee",
            tags = "Employee",
            description = "Create employee",
            summary = "Create employee",
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = EmployeeCreatedDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createEmployee(ServerRequest request) {
        return request.bodyToMono(EmployeeCreateRequestDto.class)
                      .flatMap(employeeService::createEmployee)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("employee"))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }
}
