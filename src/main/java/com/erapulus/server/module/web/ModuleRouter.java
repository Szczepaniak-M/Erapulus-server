package com.erapulus.server.module.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.*;
import static com.erapulus.server.common.web.OpenApiConstants.MODULE_BASE_URL_OPENAPI;
import static com.erapulus.server.common.web.OpenApiConstants.MODULE_DETAILS_URL_OPENAPI;
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
public class ModuleRouter {
    public static final String MODULE_BASE_URL = String.format("/api/university/{%s}/faculty/{%s}/program/{%s}/module", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM);
    public static final String MODULE_DETAILS_URL = String.format("/api/university/{%s}/faculty/{%s}/program/{%s}/module/{%s}", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM, MODULE_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = MODULE_BASE_URL_OPENAPI, method = GET, beanClass = ModuleController.class, beanMethod = "listModules"),
            @RouterOperation(path = MODULE_BASE_URL_OPENAPI, method = POST, beanClass = ModuleController.class, beanMethod = "createModule"),
            @RouterOperation(path = MODULE_DETAILS_URL_OPENAPI, method = GET, beanClass = ModuleController.class, beanMethod = "getModuleById"),
            @RouterOperation(path = MODULE_DETAILS_URL_OPENAPI, method = PUT, beanClass = ModuleController.class, beanMethod = "updateModule"),
            @RouterOperation(path = MODULE_DETAILS_URL_OPENAPI, method = DELETE, beanClass = ModuleController.class, beanMethod = "deleteModule")
    })
    @Bean
    RouterFunction<ServerResponse> moduleRoutes(ModuleController moduleController) {
        return route(GET(MODULE_BASE_URL).and(accept(APPLICATION_JSON)), moduleController::listModules)
                .andRoute(POST(MODULE_BASE_URL).and(contentType(APPLICATION_JSON)), moduleController::createModule)
                .andRoute(GET(MODULE_DETAILS_URL).and(accept(APPLICATION_JSON)), moduleController::getModuleById)
                .andRoute(PUT(MODULE_DETAILS_URL).and(contentType(APPLICATION_JSON)), moduleController::updateModule)
                .andRoute(DELETE(MODULE_DETAILS_URL).and(accept(APPLICATION_JSON)), moduleController::deleteModule);
    }
}
