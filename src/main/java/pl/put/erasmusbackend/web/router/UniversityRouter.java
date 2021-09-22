package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.UniversityController;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UniversityRouter {
    @RouterOperation(
            path = "/university",
            method = RequestMethod.GET,
            beanClass = UniversityController.class,
            beanMethod = "getUniversityList"
    )
    @Bean
    RouterFunction<ServerResponse> getUniversityRoute(UniversityController universityController) {
        return route(GET("/university"), universityController::getUniversityList);
    }
}
