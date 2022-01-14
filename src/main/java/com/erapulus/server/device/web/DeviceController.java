package com.erapulus.server.device.web;

import com.erapulus.server.common.web.ServerResponseFactory;
import com.erapulus.server.device.dto.DeviceRequestDto;
import com.erapulus.server.device.dto.DeviceResponseDto;
import com.erapulus.server.device.service.DeviceService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.erapulus.server.common.web.CommonRequestVariable.DEVICE_PATH_PARAM;
import static com.erapulus.server.common.web.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.common.web.ControllerUtils.withPathParam;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;

@Slf4j
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
                studentId -> deviceService.listDevices(studentId)
                                          .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                          .doOnError(e -> log.error(e.getMessage(), e))
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
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> createDevice(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> request.bodyToMono(DeviceRequestDto.class)
                                    .flatMap(device -> deviceService.createDevice(device, studentId))
                                    .flatMap(ServerResponseFactory::createHttpCreatedResponse)
                                    .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                    .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("device"))
                                    .doOnError(e -> log.error(e.getMessage(), e))
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
                    @ApiResponse(responseCode = "400", description = BAD_REQUEST),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN),
                    @ApiResponse(responseCode = "404", description = NOT_FOUND),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> getDeviceById(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, DEVICE_PATH_PARAM,
                        deviceId -> deviceService.getDeviceById(deviceId, studentId)
                                                 .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                                 .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                 .doOnError(e -> log.error(e.getMessage(), e))
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
                    @ApiResponse(responseCode = "409", description = CONFLICT),
                    @ApiResponse(responseCode = "500", description = INTERNAL_SERVER_ERROR)
            }
    )
    public Mono<ServerResponse> updateDevice(ServerRequest request) {
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, DEVICE_PATH_PARAM,
                        deviceId -> request.bodyToMono(DeviceRequestDto.class)
                                           .flatMap(device -> deviceService.updateDevice(device, deviceId, studentId))
                                           .flatMap(ServerResponseFactory::createHttpSuccessResponse)
                                           .onErrorResume(ConstraintViolationException.class, ServerResponseFactory::createHttpBadRequestConstraintViolationErrorResponse)
                                           .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                           .onErrorResume(DataIntegrityViolationException.class, e -> ServerResponseFactory.createHttpConflictResponse("device"))
                                           .doOnError(e -> log.error(e.getMessage(), e))
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
        return withPathParam(request, STUDENT_PATH_PARAM,
                studentId -> withPathParam(request, DEVICE_PATH_PARAM,
                        deviceId -> deviceService.deleteDevice(deviceId, studentId)
                                                 .flatMap(r -> ServerResponseFactory.createHttpNoContentResponse())
                                                 .onErrorResume(NoSuchElementException.class, ServerResponseFactory::createHttpNotFoundResponse)
                                                 .doOnError(e -> log.error(e.getMessage(), e))
                                                 .onErrorResume(e -> ServerResponseFactory.createHttpInternalServerErrorResponse())));
    }
}
