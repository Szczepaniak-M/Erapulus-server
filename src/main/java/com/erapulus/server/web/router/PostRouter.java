package com.erapulus.server.web.router;

import com.erapulus.server.web.common.CommonRequestVariable;
import com.erapulus.server.web.common.OpenApiConstants;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.erapulus.server.web.controller.PostController;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PostRouter {
    public static final String POST_BASE_URL = String.format("/api/university/{%s}/post", CommonRequestVariable.UNIVERSITY_PATH_PARAM);
    public static final String POST_DETAILS_URL = String.format("/api/university/{%s}/post/{%s}", CommonRequestVariable.UNIVERSITY_PATH_PARAM, CommonRequestVariable.POST_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = OpenApiConstants.POST_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = PostController.class, beanMethod = "listPosts"),
            @RouterOperation(path = OpenApiConstants.POST_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = PostController.class, beanMethod = "createPost"),
            @RouterOperation(path = OpenApiConstants.POST_DETAILS_URL_OPENAPI, method = RequestMethod.GET, beanClass = PostController.class, beanMethod = "getPostById"),
            @RouterOperation(path = OpenApiConstants.POST_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = PostController.class, beanMethod = "updatePost"),
            @RouterOperation(path = OpenApiConstants.POST_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = PostController.class, beanMethod = "deletePost")
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
