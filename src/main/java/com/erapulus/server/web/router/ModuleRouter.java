package com.erapulus.server.web.router;

import com.erapulus.server.web.common.CommonRequestVariable;
import com.erapulus.server.web.common.OpenApiConstants;
import com.erapulus.server.web.controller.ModuleController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ModuleRouter {
    public static final String MODULE_BASE_URL = String.format("/api/university/{%s}/faculty/{%s}/program/{%s}/module", CommonRequestVariable.UNIVERSITY_PATH_PARAM, CommonRequestVariable.FACULTY_PATH_PARAM, CommonRequestVariable.PROGRAM_PATH_PARAM);
    public static final String MODULE_DETAILS_URL = String.format("api/university/{%s}/faculty/{%s}/program/{%s}/module/{%s}", CommonRequestVariable.UNIVERSITY_PATH_PARAM, CommonRequestVariable.FACULTY_PATH_PARAM, CommonRequestVariable.PROGRAM_PATH_PARAM, CommonRequestVariable.MODULE_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = OpenApiConstants.MODULE_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = ModuleController.class, beanMethod = "listModules"),
            @RouterOperation(path = OpenApiConstants.MODULE_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = ModuleController.class, beanMethod = "createModule"),
            @RouterOperation(path = OpenApiConstants.MODULE_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = ModuleController.class, beanMethod = "getModuleById"),
            @RouterOperation(path = OpenApiConstants.MODULE_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = ModuleController.class, beanMethod = "updateModule"),
            @RouterOperation(path = OpenApiConstants.MODULE_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = ModuleController.class, beanMethod = "deleteModule")
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
