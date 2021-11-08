package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.StudentController;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class StudentRouter {
    @RouterOperation(
            path = "/student/{id}",
            method = RequestMethod.GET,
            beanClass = StudentController.class,
            beanMethod = "getStudent"
    )
    @Bean
    RouterFunction<ServerResponse> getStudentRoute(StudentController studentController) {
        return route(GET("/student/{id}").and(contentType(APPLICATION_JSON)), studentController::getStudent);
    }
}
