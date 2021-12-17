package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.StudentController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.STUDENT_DETAILS_URL_OPENAPI;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StudentRouter {
    public static final String STUDENT_BASE_URL = "/api/student";
    public static final String STUDENT_DETAILS_URL = format("/api/student/{%s}", STUDENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = GET, beanClass = StudentController.class, beanMethod = "getStudentById"),
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = PUT, beanClass = StudentController.class, beanMethod = "updateStudent"),
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = DELETE, beanClass = StudentController.class, beanMethod = "deleteStudent")
    })
    @Bean
    RouterFunction<ServerResponse> studentRoutes(StudentController studentController) {
        return route(GET(STUDENT_DETAILS_URL).and(accept(APPLICATION_JSON)), studentController::getStudentById)
                .andRoute(PUT(STUDENT_DETAILS_URL).and(contentType(APPLICATION_JSON)), studentController::updateStudent)
                .andRoute(DELETE(STUDENT_DETAILS_URL).and(accept(APPLICATION_JSON)), studentController::deleteStudent);
    }
}
