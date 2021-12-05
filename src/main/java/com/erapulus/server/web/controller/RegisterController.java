package com.erapulus.server.web.controller;

import com.erapulus.server.web.common.OpenApiConstants;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.erapulus.server.dto.EmployeeCreateRequestDto;
import com.erapulus.server.dto.EmployeeCreatedDto;
import com.erapulus.server.service.RegisterService;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;

@RestController
@AllArgsConstructor
public class RegisterController {

    private RegisterService registerService;

    @NonNull
    @Operation(
            operationId = "create-employee",
            tags = "Employee",
            description = "Create employee",
            summary = "Create employee",
            responses = {
                    @ApiResponse(responseCode = "201", description = OpenApiConstants.OK, content = @Content(schema = @Schema(implementation = EmployeeCreatedDto.class))),
                    @ApiResponse(responseCode = "400", description = OpenApiConstants.BAD_REQUEST),
                    @ApiResponse(responseCode = "409", description = OpenApiConstants.CONFLICT),
                    @ApiResponse(responseCode = "500", description = OpenApiConstants.INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createEmployee(ServerRequest request) {
        return request.bodyToMono(EmployeeCreateRequestDto.class)
                      .flatMap(registerService::createEmployee)
                      .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                      .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                      .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("employee"))
                      .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                      .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse());
    }
}
