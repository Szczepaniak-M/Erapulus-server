package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.ApplicationUserController;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApplicationUserRouter {

    public static final String USER_URL = "/api/user";

    @RouterOperation(path = USER_URL, method = GET, beanClass = ApplicationUserController.class, beanMethod = "listApplicationUsers")
    @Bean
    RouterFunction<ServerResponse> applicationUserRoutes(ApplicationUserController applicationUserController) {
        return route(GET(USER_URL).and(accept(APPLICATION_JSON)), applicationUserController::listApplicationUsers);
    }
}
