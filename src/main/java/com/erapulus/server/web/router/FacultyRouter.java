package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.FacultyController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.FACULTY_PATH_PARAM;
import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.FACULTY_BASE_URL_OPENAPI;
import static com.erapulus.server.web.common.OpenApiConstants.FACULTY_DETAILS_URL_OPENAPI;
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
public class FacultyRouter {

    public static final String FACULTY_BASE_URL = String.format("/api/university/{%s}/faculty", UNIVERSITY_PATH_PARAM);
    public static final String FACULTY_DETAILS_URL = String.format("/api/university/{%s}/faculty/{%s}", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = FACULTY_BASE_URL_OPENAPI, method = GET, beanClass = FacultyController.class, beanMethod = "listFaculties"),
            @RouterOperation(path = FACULTY_BASE_URL_OPENAPI, method = POST, beanClass = FacultyController.class, beanMethod = "createFaculty"),
            @RouterOperation(path = FACULTY_DETAILS_URL_OPENAPI, method = GET, beanClass = FacultyController.class, beanMethod = "getFacultyById"),
            @RouterOperation(path = FACULTY_DETAILS_URL_OPENAPI, method = PUT, beanClass = FacultyController.class, beanMethod = "updateFaculty"),
            @RouterOperation(path = FACULTY_DETAILS_URL_OPENAPI, method = DELETE, beanClass = FacultyController.class, beanMethod = "deleteFaculty")
    })
    @Bean
    RouterFunction<ServerResponse> facultyRoutes(FacultyController facultyController) {
        return route(GET(FACULTY_BASE_URL).and(accept(APPLICATION_JSON)), facultyController::listFaculties)
                .andRoute(POST(FACULTY_BASE_URL).and(contentType(APPLICATION_JSON)), facultyController::createFaculty)
                .andRoute(GET(FACULTY_DETAILS_URL).and(accept(APPLICATION_JSON)), facultyController::getFacultyById)
                .andRoute(PUT(FACULTY_DETAILS_URL).and(contentType(APPLICATION_JSON)), facultyController::updateFaculty)
                .andRoute(DELETE(FACULTY_DETAILS_URL).and(accept(APPLICATION_JSON)), facultyController::deleteFaculty);
    }
}
