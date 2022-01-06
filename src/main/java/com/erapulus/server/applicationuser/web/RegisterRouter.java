package com.erapulus.server.applicationuser.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RegisterRouter {

    public static final String REGISTER_ADMINISTRATOR = "/api/user/register/administrator";
    public static final String REGISTER_UNIVERSITY_ADMINISTRATOR = "/api/user/register/university-administrator";
    public static final String REGISTER_UNIVERSITY_EMPLOYEE = "/api/user/register/employee";

    @RouterOperations({
            @RouterOperation(path = REGISTER_ADMINISTRATOR, method = POST, beanClass = RegisterController.class, beanMethod = "createAdministrator"),
            @RouterOperation(path = REGISTER_UNIVERSITY_ADMINISTRATOR, method = POST, beanClass = RegisterController.class, beanMethod = "createUniversityAdministrator"),
            @RouterOperation(path = REGISTER_UNIVERSITY_EMPLOYEE, method = POST, beanClass = RegisterController.class, beanMethod = "createUniversityEmployee")
    })
    @Bean
    RouterFunction<ServerResponse> registerRoute(RegisterController registerController) {
        return route(POST(REGISTER_ADMINISTRATOR).and(contentType(APPLICATION_JSON)), registerController::createAdministrator)
                .andRoute(POST(REGISTER_UNIVERSITY_ADMINISTRATOR).and(contentType(APPLICATION_JSON)), registerController::createUniversityAdministrator)
                .andRoute(POST(REGISTER_UNIVERSITY_EMPLOYEE).and(contentType(APPLICATION_JSON)), registerController::createUniversityEmployee);
    }
}
