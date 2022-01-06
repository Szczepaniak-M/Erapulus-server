package com.erapulus.server.program.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.*;
import static com.erapulus.server.common.web.OpenApiConstants.PROGRAM_BASE_URL_OPENAPI;
import static com.erapulus.server.common.web.OpenApiConstants.PROGRAM_DETAILS_URL_OPENAPI;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProgramRouter {
    public static final String PROGRAM_BASE_URL = String.format("/api/university/{%s}/faculty/{%s}/program", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM);
    public static final String PROGRAM_DETAILS_URL = String.format("/api/university/{%s}/faculty/{%s}/program/{%s}", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = PROGRAM_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = ProgramController.class, beanMethod = "listPrograms"),
            @RouterOperation(path = PROGRAM_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = ProgramController.class, beanMethod = "createProgram"),
            @RouterOperation(path = PROGRAM_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = ProgramController.class, beanMethod = "getProgramById"),
            @RouterOperation(path = PROGRAM_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = ProgramController.class, beanMethod = "updateProgram"),
            @RouterOperation(path = PROGRAM_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = ProgramController.class, beanMethod = "deleteProgram")
    })
    @Bean
    RouterFunction<ServerResponse> programRoutes(ProgramController programController) {
        return route(GET(PROGRAM_BASE_URL).and(accept(APPLICATION_JSON)), programController::listPrograms)
                .andRoute(POST(PROGRAM_BASE_URL).and(contentType(APPLICATION_JSON)), programController::createProgram)
                .andRoute(GET(PROGRAM_DETAILS_URL).and(accept(APPLICATION_JSON)), programController::getProgramById)
                .andRoute(PUT(PROGRAM_DETAILS_URL).and(contentType(APPLICATION_JSON)), programController::updateProgram)
                .andRoute(DELETE(PROGRAM_DETAILS_URL).and(accept(APPLICATION_JSON)), programController::deleteProgram);
    }
}
