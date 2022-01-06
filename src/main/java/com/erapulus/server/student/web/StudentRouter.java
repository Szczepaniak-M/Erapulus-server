package com.erapulus.server.student.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.common.web.OpenApiConstants.*;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StudentRouter {

    public static final String STUDENT_BASE_URL = "/api/student";
    public static final String STUDENT_DETAILS_URL = format("/api/student/{%s}", STUDENT_PATH_PARAM);
    public static final String STUDENT_UPDATE_UNIVERSITY_URL = format("/api/student/{%s}/university", STUDENT_PATH_PARAM);
    public static final String STUDENT_UPDATE_PHOTO_URL = format("/api/student/{%s}/photo", STUDENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = STUDENT_BASE_URL_OPENAPI, method = GET, beanClass = StudentController.class, beanMethod = "listStudents"),
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = GET, beanClass = StudentController.class, beanMethod = "getStudentById"),
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = PUT, beanClass = StudentController.class, beanMethod = "updateStudent"),
            @RouterOperation(path = STUDENT_UPDATE_UNIVERSITY_URL_OPENAPI, method = PATCH, beanClass = StudentController.class, beanMethod = "updateStudentUniversity"),
            @RouterOperation(path = STUDENT_UPDATE_PHOTO_URL_OPENAPI, method = PATCH, beanClass = StudentController.class, beanMethod = "updateStudentPhoto")
    })
    @Bean
    RouterFunction<ServerResponse> studentRoutes(StudentController studentController) {
        return route(GET(STUDENT_BASE_URL).and(accept(APPLICATION_JSON)), studentController::listStudents)
                .andRoute(GET(STUDENT_DETAILS_URL).and(accept(APPLICATION_JSON)), studentController::getStudentById)
                .andRoute(PUT(STUDENT_DETAILS_URL).and(contentType(APPLICATION_JSON)), studentController::updateStudent)
                .andRoute(PATCH(STUDENT_UPDATE_UNIVERSITY_URL).and(contentType(APPLICATION_JSON)), studentController::updateStudentUniversity)
                .andRoute(PATCH(STUDENT_UPDATE_PHOTO_URL).and(contentType(MULTIPART_FORM_DATA)), studentController::updateStudentPhoto);
    }
}
