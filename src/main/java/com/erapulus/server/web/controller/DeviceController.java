package com.erapulus.server.web.controller;

import com.erapulus.server.dto.DeviceRequestDto;
import com.erapulus.server.dto.DeviceResponseDto;
import com.erapulus.server.service.DeviceService;
import com.erapulus.server.web.common.ServerResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.erapulus.server.web.common.CommonRequestVariable.DEVICE_PATH_PARAM;
import static com.erapulus.server.web.common.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static com.erapulus.server.web.controller.ControllerUtils.withPathParam;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@RestController
@AllArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @NonNull
    @Operation(
            operationId = "list-device",
            tags = "Device",
            description = "List device",
            summary = "List device by user ID",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> listDevices(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> deviceService.listEntities(studentId)
                                          .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                          .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "create-device",
            tags = "Device",
            description = "Create device",
            summary = "Create device",
            parameters = @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = DeviceRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = OK, content = @Content(schema = @Schema(implementation = DeviceResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createDevice(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> request.bodyToMono(DeviceRequestDto.class)
                                    .flatMap(device -> deviceService.createEntity(device, studentId))
                                    .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                    .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                    .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse()));
    }

    @NonNull
    @Operation(
            operationId = "get-device",
            tags = "Device",
            description = "Get device by ID",
            summary = "Get device by ID",
            parameters = {
                    @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = DEVICE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = DeviceResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getDeviceById(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, DEVICE_PATH_PARAM,
                        deviceId -> deviceService.getEntityById(deviceId, studentId)
                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                 .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("post"))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "update-device",
            tags = "Device",
            description = "Update device",
            summary = "Update device",
            parameters = {
                    @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = DEVICE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = DeviceRequestDto.class)), required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = OK, content = @Content(schema = @Schema(implementation = DeviceResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateDevice(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, DEVICE_PATH_PARAM,
                        deviceId -> request.bodyToMono(DeviceRequestDto.class)
                                           .flatMap(device -> deviceService.updateEntity(device, deviceId, studentId))
                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                           .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                           .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                           .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())
                                           .switchIfEmpty(ServerResponseFactory.createHttpBadRequestNoBodyFoundErrorResponse())));
    }

    @NonNull
    @Operation(
            operationId = "delete-device",
            tags = "Device",
            description = "Delete device",
            summary = "Delete device by ID",
            parameters = {
                    @Parameter(in = PATH, name = STUDENT_PATH_PARAM, schema = @Schema(type = "integer"), required = true),
                    @Parameter(in = PATH, name = DEVICE_PATH_PARAM, schema = @Schema(type = "integer"), required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = NO_CONTENT),
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> deleteDevice(ServerRequest request) {
        return withPathParam(request, DEVICE_PATH_PARAM,
                facultyId -> deviceService.deleteEntity(facultyId)
                                          .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                          .onErrorResume(NoSuchElementException.class, e -> ServerResponseFactory.createHttpNotFoundResponse("building"))
                                          .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse()));
    }
}