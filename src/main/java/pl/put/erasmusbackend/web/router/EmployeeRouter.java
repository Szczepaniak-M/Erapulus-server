package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.EmployeeController;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EmployeeRouter {
    @RouterOperation(
            path = "/register/employee",
            method = RequestMethod.POST,
            beanClass = EmployeeRouter.class,
            beanMethod = "createEmployee"
    )
    @Bean
    RouterFunction<ServerResponse> createEmployeeRoute(EmployeeController employeeController) {
        return route(POST("/register/employee").and(contentType(APPLICATION_JSON)), employeeController::createEmployee);
    }
}
