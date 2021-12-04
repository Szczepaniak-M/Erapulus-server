package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.FacultyController;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static pl.put.erasmusbackend.web.common.CommonRequestVariable.FACULTY_PATH_PARAM;
import static pl.put.erasmusbackend.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static pl.put.erasmusbackend.web.common.OpenApiConstants.FACULTY_BASE_URL_OPENAPI;
import static pl.put.erasmusbackend.web.common.OpenApiConstants.FACULTY_DETAILS_URL_OPENAPI;

@Configuration
public class FacultyRouter {

    public static final String FACULTY_BASE_URL = format("/api/university/{%s}/faculty", UNIVERSITY_PATH_PARAM);
    public static final String FACULTY_DETAILS_URL = format("/api/university/{%s}/faculty/{%s}", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = FACULTY_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = FacultyController.class, beanMethod = "listFaculties"),
            @RouterOperation(path = FACULTY_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = FacultyController.class, beanMethod = "createFaculty"),
            @RouterOperation(path = FACULTY_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = FacultyController.class, beanMethod = "getFacultyById"),
            @RouterOperation(path = FACULTY_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = FacultyController.class, beanMethod = "updateFaculty"),
            @RouterOperation(path = FACULTY_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = FacultyController.class, beanMethod = "deleteFaculty")
    })
    @Bean
    RouterFunction<ServerResponse> facultyRoutes(FacultyController facultyController) {
        return route(GET(FACULTY_BASE_URL).and(accept(APPLICATION_JSON)), facultyController::listFaculties)
                .andRoute(POST(FACULTY_BASE_URL).and(contentType(APPLICATION_JSON)), facultyController::createFaculty)
                .andRoute(GET(FACULTY_DETAILS_URL).and(accept(APPLICATION_JSON)), facultyController::getFacultyById)
                .andRoute(PUT(FACULTY_DETAILS_URL).and(contentType(APPLICATION_JSON)), facultyController::updateFaculty)
                .andRoute(DELETE(FACULTY_DETAILS_URL).and(accept(APPLICATION_JSON)), facultyController::deleteFaculty);
    }
}
