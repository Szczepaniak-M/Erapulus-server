package com.erapulus.server.applicationuser.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.USER_PATH_PARAM;
import static com.erapulus.server.common.web.OpenApiConstants.USER_BASE_URL_OPENAPI;
import static com.erapulus.server.common.web.OpenApiConstants.USER_DETAILS_URL_OPENAPI;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApplicationUserRouter {

    public static final String USER_BASE_URL = "/api/user";
    public static final String USER_DETAILS_URL = format("/api/user/{%s}", USER_PATH_PARAM);


    @RouterOperations({
            @RouterOperation(path = USER_BASE_URL_OPENAPI, method = GET, beanClass = ApplicationUserController.class, beanMethod = "listApplicationUsers"),
            @RouterOperation(path = USER_DETAILS_URL_OPENAPI, method = DELETE, beanClass = ApplicationUserController.class, beanMethod = "deleteApplicationUser")
    })
    @Bean
    RouterFunction<ServerResponse> applicationUserRoutes(ApplicationUserController applicationUserController) {
        return route(GET(USER_BASE_URL).and(accept(APPLICATION_JSON)), applicationUserController::listApplicationUsers)
                .andRoute(DELETE(USER_DETAILS_URL).and(accept(APPLICATION_JSON)), applicationUserController::deleteApplicationUser);
    }
}
