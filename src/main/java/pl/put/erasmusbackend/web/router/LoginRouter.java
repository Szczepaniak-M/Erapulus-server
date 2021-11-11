package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.LoginController;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoginRouter {

    @RouterOperation(
            path = "/login/employee",
            method = RequestMethod.POST,
            beanClass = LoginController.class,
            beanMethod = "loginEmployee"
    )
    @Bean
    RouterFunction<ServerResponse> loginEmployeeRoute(LoginController loginController) {
        return route(POST("/login/employee").and(contentType(APPLICATION_JSON)), loginController::loginEmployee);
    }

    @RouterOperation(
            path = "/login/google",
            method = RequestMethod.POST,
            beanClass = LoginController.class,
            beanMethod = "loginEmployee"
    )
    @Bean
    RouterFunction<ServerResponse> loginStudentGoogleRoute(LoginController loginController) {
        return route(POST("/login/google").and(contentType(APPLICATION_JSON)), loginController::loginStudentGoogle);
    }
}

