package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.PostController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.POST_PATH_PARAM;
import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.web.common.OpenApiConstants.POST_BASE_URL_OPENAPI;
import static com.erapulus.server.web.common.OpenApiConstants.POST_DETAILS_URL_OPENAPI;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PostRouter {
    public static final String POST_BASE_URL = String.format("/api/university/{%s}/post", UNIVERSITY_PATH_PARAM);
    public static final String POST_DETAILS_URL = String.format("/api/university/{%s}/post/{%s}", UNIVERSITY_PATH_PARAM, POST_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = POST_BASE_URL_OPENAPI, method = GET, beanClass = PostController.class, beanMethod = "listPosts"),
            @RouterOperation(path = POST_BASE_URL_OPENAPI, method = POST, beanClass = PostController.class, beanMethod = "createPost"),
            @RouterOperation(path = POST_DETAILS_URL_OPENAPI, method = GET, beanClass = PostController.class, beanMethod = "getPostById"),
            @RouterOperation(path = POST_DETAILS_URL_OPENAPI, method = PUT, beanClass = PostController.class, beanMethod = "updatePost"),
            @RouterOperation(path = POST_DETAILS_URL_OPENAPI, method = DELETE, beanClass = PostController.class, beanMethod = "deletePost")
    })
    @Bean
    RouterFunction<ServerResponse> postRoutes(PostController postController) {
        return route(GET(POST_BASE_URL).and(accept(APPLICATION_JSON)), postController::listPosts)
                .andRoute(POST(POST_BASE_URL).and(contentType(APPLICATION_JSON)), postController::createPost)
                .andRoute(GET(POST_DETAILS_URL).and(accept(APPLICATION_JSON)), postController::getPostById)
                .andRoute(PUT(POST_DETAILS_URL).and(contentType(APPLICATION_JSON)), postController::updatePost)
                .andRoute(DELETE(POST_DETAILS_URL).and(accept(APPLICATION_JSON)), postController::deletePost);
    }
}
