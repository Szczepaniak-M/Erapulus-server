package com.erapulus.server.web.router;

import com.erapulus.server.web.common.CommonRequestVariable;
import com.erapulus.server.web.common.OpenApiConstants;
import com.erapulus.server.web.controller.FacultyController;
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
public class FacultyRouter {

    public static final String FACULTY_BASE_URL = String.format("/api/university/{%s}/faculty", CommonRequestVariable.UNIVERSITY_PATH_PARAM);
    public static final String FACULTY_DETAILS_URL = String.format("/api/university/{%s}/faculty/{%s}", CommonRequestVariable.UNIVERSITY_PATH_PARAM, CommonRequestVariable.FACULTY_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = OpenApiConstants.FACULTY_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = FacultyController.class, beanMethod = "listFaculties"),
            @RouterOperation(path = OpenApiConstants.FACULTY_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = FacultyController.class, beanMethod = "createFaculty"),
            @RouterOperation(path = OpenApiConstants.FACULTY_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = FacultyController.class, beanMethod = "getFacultyById"),
            @RouterOperation(path = OpenApiConstants.FACULTY_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = FacultyController.class, beanMethod = "updateFaculty"),
            @RouterOperation(path = OpenApiConstants.FACULTY_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = FacultyController.class, beanMethod = "deleteFaculty")
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
