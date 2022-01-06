package com.erapulus.server.device.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.DEVICE_PATH_PARAM;
import static com.erapulus.server.common.web.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.common.web.OpenApiConstants.DEVICE_BASE_URL_OPENAPI;
import static com.erapulus.server.common.web.OpenApiConstants.DEVICE_DETAILS_URL_OPENAPI;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class DeviceRouter {
    public static final String DEVICE_BASE_URL = String.format("/api/student/{%s}/device", STUDENT_PATH_PARAM);
    public static final String DEVICE_DETAILS_URL = String.format("/api/student/{%s}/device/{%s}", STUDENT_PATH_PARAM, DEVICE_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = DEVICE_BASE_URL_OPENAPI, method = GET, beanClass = DeviceController.class, beanMethod = "listDevices"),
            @RouterOperation(path = DEVICE_BASE_URL_OPENAPI, method = POST, beanClass = DeviceController.class, beanMethod = "createDevice"),
            @RouterOperation(path = DEVICE_DETAILS_URL_OPENAPI, method = GET, beanClass = DeviceController.class, beanMethod = "getDeviceById"),
            @RouterOperation(path = DEVICE_DETAILS_URL_OPENAPI, method = PUT, beanClass = DeviceController.class, beanMethod = "updateDevice"),
            @RouterOperation(path = DEVICE_DETAILS_URL_OPENAPI, method = DELETE, beanClass = DeviceController.class, beanMethod = "deleteDevice")
    })
    @Bean
    RouterFunction<ServerResponse> deviceRoutes(DeviceController deviceController) {
        return route(GET(DEVICE_BASE_URL).and(accept(APPLICATION_JSON)), deviceController::listDevices)
                .andRoute(POST(DEVICE_BASE_URL).and(contentType(APPLICATION_JSON)), deviceController::createDevice)
                .andRoute(GET(DEVICE_DETAILS_URL).and(accept(APPLICATION_JSON)), deviceController::getDeviceById)
                .andRoute(PUT(DEVICE_DETAILS_URL).and(contentType(APPLICATION_JSON)), deviceController::updateDevice)
                .andRoute(DELETE(DEVICE_DETAILS_URL).and(accept(APPLICATION_JSON)), deviceController::deleteDevice);
    }
}
