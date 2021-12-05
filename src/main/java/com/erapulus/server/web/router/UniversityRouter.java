package com.erapulus.server.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.erapulus.server.web.controller.UniversityController;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.UNIVERSITY_BASE_URL_OPENAPI;
import static com.erapulus.server.web.common.OpenApiConstants.UNIVERSITY_DETAILS_URL_OPENAPI;

@Configuration
public class UniversityRouter {

    public static final String UNIVERSITY_BASE_URL = "/api/university";
    public static final String UNIVERSITY_DETAILS_URL = format("/api/university/{%s}/", UNIVERSITY_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = UNIVERSITY_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = UniversityController.class, beanMethod = "listUniversities"),
            @RouterOperation(path = UNIVERSITY_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = UniversityController.class, beanMethod = "createUniversity"),
            @RouterOperation(path = UNIVERSITY_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = UniversityController.class, beanMethod = "getUniversityById"),
            @RouterOperation(path = UNIVERSITY_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = UniversityController.class, beanMethod = "updateUniversity"),
            @RouterOperation(path = UNIVERSITY_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = UniversityController.class, beanMethod = "deleteUniversity")
    })
    @Bean
    RouterFunction<ServerResponse> universityRoutes(UniversityController universityController) {
        return route(GET(UNIVERSITY_BASE_URL).and(accept(APPLICATION_JSON)), universityController::listUniversities)
                .andRoute(POST(UNIVERSITY_BASE_URL).and(contentType(APPLICATION_JSON)), universityController::createUniversity)
                .andRoute(GET(UNIVERSITY_DETAILS_URL).and(accept(APPLICATION_JSON)), universityController::getUniversityById)
                .andRoute(PUT(UNIVERSITY_DETAILS_URL).and(contentType(APPLICATION_JSON)), universityController::updateUniversity)
                .andRoute(DELETE(UNIVERSITY_DETAILS_URL).and(accept(APPLICATION_JSON)), universityController::deleteUniversity);
    }
}
