package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.RegisterController;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RegisterRouter {

    public static final String REGISTER_EMPLOYEE = "/api/register/employee";

    @RouterOperations({
            @RouterOperation(path = REGISTER_EMPLOYEE, method = RequestMethod.POST, beanClass = RegisterController.class, beanMethod = "createEmployee")
    })
    @Bean
    RouterFunction<ServerResponse> registerRoute(RegisterController registerController) {
        return route(POST(REGISTER_EMPLOYEE).and(contentType(APPLICATION_JSON)), registerController::createEmployee);
    }
}
