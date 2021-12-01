package pl.put.erasmusbackend.web.router;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.put.erasmusbackend.web.controller.PostController;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static pl.put.erasmusbackend.web.common.CommonRequestVariable.*;
import static pl.put.erasmusbackend.web.common.OpenApiConstants.*;

@Configuration
public class PostRouter {
    public static final String POST_BASE_URL = format("/api/university/{%s}/post", UNIVERSITY_PATH_PARAM);
    public static final String POST_DETAILS_URL = format("/api/university/{%s}/post/{%s}", UNIVERSITY_PATH_PARAM, POST_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = POST_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = PostController.class, beanMethod = "listPostsByUniversity"),
            @RouterOperation(path = POST_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = PostController.class, beanMethod = "createPost"),
            @RouterOperation(path = POST_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = PostController.class, beanMethod = "getPostById"),
            @RouterOperation(path = POST_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = PostController.class, beanMethod = "updatePost"),
            @RouterOperation(path = POST_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = PostController.class, beanMethod = "deletePost")
    })
    @Bean
    RouterFunction<ServerResponse> postRoutes(PostController postController) {
        return route(GET(POST_BASE_URL).and(contentType(APPLICATION_JSON)), postController::listPostsByUniversity)
                .andRoute(POST(POST_BASE_URL).and(contentType(APPLICATION_JSON)), postController::createPost)
                .andRoute(GET(POST_DETAILS_URL).and(contentType(APPLICATION_JSON)), postController::getPostById)
                .andRoute(PUT(POST_DETAILS_URL).and(contentType(APPLICATION_JSON)), postController::updatePost)
                .andRoute(DELETE(POST_DETAILS_URL).and(contentType(APPLICATION_JSON)), postController::deletePost);
    }
}
