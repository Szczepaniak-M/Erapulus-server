package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.LoginController;
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
public class LoginRouter {

    public static final String LOGIN_EMPLOYEE = "/api/user/login/employee";
    public static final String LOGIN_GOOGLE = "/api/user/login/google";

    @RouterOperations({
            @RouterOperation(path = LOGIN_EMPLOYEE, method = POST, beanClass = LoginController.class, beanMethod = "loginEmployee"),
            @RouterOperation(path = LOGIN_GOOGLE, method = POST, beanClass = LoginController.class, beanMethod = "loginStudentGoogle")
    })
    @Bean
    RouterFunction<ServerResponse> loginRoute(LoginController loginController) {
        return route(POST(LOGIN_EMPLOYEE).and(contentType(APPLICATION_JSON)), loginController::loginEmployee)
                .andRoute(POST(LOGIN_GOOGLE).and(contentType(APPLICATION_JSON)), loginController::loginStudentGoogle);
    }
}

