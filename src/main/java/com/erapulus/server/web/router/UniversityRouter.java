package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.UniversityController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class UniversityRouter {

    public static final String UNIVERSITY_BASE_URL = "/api/university";
    public static final String UNIVERSITY_DETAILS_URL = format("/api/university/{%s}", UNIVERSITY_PATH_PARAM);
    public static final String UNIVERSITY_UPDATE_LOGO_URL = format("/api/university/{%s}/logo", UNIVERSITY_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = UNIVERSITY_BASE_URL_OPENAPI, method = GET, beanClass = UniversityController.class, beanMethod = "listUniversities"),
            @RouterOperation(path = UNIVERSITY_BASE_URL_OPENAPI, method = POST, beanClass = UniversityController.class, beanMethod = "createUniversity"),
            @RouterOperation(path = UNIVERSITY_DETAILS_URL_OPENAPI, method = GET, beanClass = UniversityController.class, beanMethod = "getUniversityById"),
            @RouterOperation(path = UNIVERSITY_DETAILS_URL_OPENAPI, method = PUT, beanClass = UniversityController.class, beanMethod = "updateUniversity"),
            @RouterOperation(path = UNIVERSITY_DETAILS_URL_OPENAPI, method = DELETE, beanClass = UniversityController.class, beanMethod = "deleteUniversity"),
            @RouterOperation(path = UNIVERSITY_UPDATE_LOGO_URL_OPENAPI, method = PATCH, beanClass = UniversityController.class, beanMethod = "updateUniversityPhoto")
    })
    @Bean
    RouterFunction<ServerResponse> universityRoutes(UniversityController universityController) {
        return route(GET(UNIVERSITY_BASE_URL).and(accept(APPLICATION_JSON)), universityController::listUniversities)
                .andRoute(POST(UNIVERSITY_BASE_URL).and(contentType(APPLICATION_JSON)), universityController::createUniversity)
                .andRoute(GET(UNIVERSITY_DETAILS_URL).and(accept(APPLICATION_JSON)), universityController::getUniversityById)
                .andRoute(PUT(UNIVERSITY_DETAILS_URL).and(contentType(APPLICATION_JSON)), universityController::updateUniversity)
                .andRoute(DELETE(UNIVERSITY_DETAILS_URL).and(accept(APPLICATION_JSON)), universityController::deleteUniversity)
                .andRoute(PATCH(UNIVERSITY_UPDATE_LOGO_URL).and(contentType(MULTIPART_FORM_DATA)), universityController::updateUniversityPhoto);
    }
}
