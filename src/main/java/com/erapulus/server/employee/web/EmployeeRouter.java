package com.erapulus.server.employee.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.EMPLOYEE_PATH_PARAM;
import static com.erapulus.server.common.web.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.common.web.OpenApiConstants.EMPLOYEE_DETAILS_URL_OPENAPI;
import static com.erapulus.server.common.web.OpenApiConstants.EMPLOYEE_LIST_URL_OPENAPI;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class EmployeeRouter {
    public static final String EMPLOYEE_LIST_URL = format("/api/university/{%s}/employee", UNIVERSITY_PATH_PARAM);
    public static final String EMPLOYEE_DETAILS_URL = format("/api/employee/{%s}", EMPLOYEE_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = EMPLOYEE_LIST_URL_OPENAPI, method = GET, beanClass = EmployeeController.class, beanMethod = "listEmployeeFromUniversity"),
            @RouterOperation(path = EMPLOYEE_DETAILS_URL_OPENAPI, method = GET, beanClass = EmployeeController.class, beanMethod = "getEmployeeById"),
            @RouterOperation(path = EMPLOYEE_DETAILS_URL_OPENAPI, method = PUT, beanClass = EmployeeController.class, beanMethod = "updateEmployee"),
    })
    @Bean
    RouterFunction<ServerResponse> employeeRoutes(EmployeeController employeeController) {
        return route(GET(EMPLOYEE_LIST_URL).and(accept(APPLICATION_JSON)), employeeController::listEmployeeFromUniversity)
                .andRoute(GET(EMPLOYEE_DETAILS_URL).and(contentType(APPLICATION_JSON)), employeeController::getEmployeeById)
                .andRoute(PUT(EMPLOYEE_DETAILS_URL).and(contentType(APPLICATION_JSON)), employeeController::updateEmployee);
    }
}
