package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.StudentController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.STUDENT_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StudentRouter {

    public static final String STUDENT_DETAILS_URL = format("/api/student/{%s}", STUDENT_PATH_PARAM);
    public static final String STUDENT_LIST_FRIENDS_URL = format("/api/student/{%s}/friend", STUDENT_PATH_PARAM);
    public static final String STUDENT_UPDATE_UNIVERSITY_URL = format("/api/student/{%s}/university", STUDENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = GET, beanClass = StudentController.class, beanMethod = "getStudentById"),
            @RouterOperation(path = STUDENT_DETAILS_URL_OPENAPI, method = PUT, beanClass = StudentController.class, beanMethod = "updateStudent"),
            @RouterOperation(path = STUDENT_LIST_FRIENDS_URL_OPENAPI, method = GET, beanClass = StudentController.class, beanMethod = "listFriends"),
            @RouterOperation(path = STUDENT_UPDATE_UNIVERSITY_URL_OPENAPI, method = PATCH, beanClass = StudentController.class, beanMethod = "updateStudentUniversity")
    })
    @Bean
    RouterFunction<ServerResponse> studentRoutes(StudentController studentController) {
        return route(GET(STUDENT_DETAILS_URL).and(accept(APPLICATION_JSON)), studentController::getStudentById)
                .andRoute(PUT(STUDENT_DETAILS_URL).and(contentType(APPLICATION_JSON)), studentController::updateStudent)
                .andRoute(GET(STUDENT_LIST_FRIENDS_URL).and(accept(APPLICATION_JSON)), studentController::listFriends)
                .andRoute(PATCH(STUDENT_UPDATE_UNIVERSITY_URL).and(contentType(APPLICATION_JSON)), studentController::updateStudentUniversity);
    }
}
