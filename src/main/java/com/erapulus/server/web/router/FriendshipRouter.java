package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.FriendshipController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.*;
import static com.erapulus.server.web.common.OpenApiConstants.*;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FriendshipRouter {

    public static final String FRIENDS_BASE_URL = format("/api/student/{%s}/friend", STUDENT_PATH_PARAM);
    public static final String FRIENDS_DETAILS_URL = format("/api/student/{%s}/friend/{%s}", STUDENT_PATH_PARAM, FRIEND_PATH_PARAM);
    public static final String FRIENDS_REQUESTS_URL = format("/api/student/{%s}/friend/request", STUDENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = FRIEND_BASE_URL_OPENAPI, method = GET, beanClass = FriendshipController.class, beanMethod = "listFriends"),
            @RouterOperation(path = FRIEND_REQUESTS_URL_OPENAPI, method = GET, beanClass = FriendshipController.class, beanMethod = "listFriendRequests"),
            @RouterOperation(path = FRIEND_BASE_URL_OPENAPI, method = POST, beanClass = FriendshipController.class, beanMethod = "addFriend"),
            @RouterOperation(path = FRIEND_DETAILS_URL_OPENAPI, method = POST, beanClass = FriendshipController.class, beanMethod = "handleFriendshipRequest"),
            @RouterOperation(path = FRIEND_DETAILS_URL_OPENAPI, method = DELETE, beanClass = FriendshipController.class, beanMethod = "deleteFriend")
    })
    @Bean
    RouterFunction<ServerResponse> friendRoutes(FriendshipController friendshipController) {
        return route(GET(FRIENDS_BASE_URL).and(accept(APPLICATION_JSON)), friendshipController::listFriends)
                .andRoute(GET(FRIENDS_REQUESTS_URL).and(accept(APPLICATION_JSON)), friendshipController::listFriendRequests)
                .andRoute(POST(FRIENDS_BASE_URL).and(contentType(APPLICATION_JSON)), friendshipController::addFriend)
                .andRoute(POST(FRIENDS_DETAILS_URL).and(contentType(APPLICATION_JSON)), friendshipController::handleFriendshipRequest)
                .andRoute(DELETE(FRIENDS_DETAILS_URL).and(accept(APPLICATION_JSON)), friendshipController::deleteFriend);
    }
}
