package com.erapulus.server.web.router;

import com.erapulus.server.web.common.OpenApiConstants;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.erapulus.server.web.controller.BuildingController;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static com.erapulus.server.web.common.CommonRequestVariable.BUILDING_PATH_PARAM;
import static com.erapulus.server.web.common.CommonRequestVariable.UNIVERSITY_PATH_PARAM;

@Configuration
public class BuildingRouter {

    public static final String BUILDING_BASE_URL = format("/api/university/{%s}/building", UNIVERSITY_PATH_PARAM);
    public static final String BUILDING_DETAILS_URL = format("/api/university/{%s}/building/{%s}", UNIVERSITY_PATH_PARAM, BUILDING_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = OpenApiConstants.BUILDING_BASE_URL_OPENAPI, method = RequestMethod.GET, beanClass = BuildingController.class, beanMethod = "listBuildings"),
            @RouterOperation(path = OpenApiConstants.BUILDING_BASE_URL_OPENAPI, method = RequestMethod.POST, beanClass = BuildingController.class, beanMethod = "createBuilding"),
            @RouterOperation(path = OpenApiConstants.BUILDING_DETAILS_URL_OPENAPI, method = RequestMethod.PUT, beanClass = BuildingController.class, beanMethod = "updateBuilding"),
            @RouterOperation(path = OpenApiConstants.BUILDING_DETAILS_URL_OPENAPI, method = RequestMethod.DELETE, beanClass = BuildingController.class, beanMethod = "deleteBuilding")
    })
    @Bean
    RouterFunction<ServerResponse> buildingRoutes(BuildingController buildingController) {
        return route(GET(BUILDING_BASE_URL).and(accept(APPLICATION_JSON)), buildingController::listBuildings)
                .andRoute(POST(BUILDING_BASE_URL).and(contentType(APPLICATION_JSON)), buildingController::createBuilding)
                .andRoute(PUT(BUILDING_DETAILS_URL).and(contentType(APPLICATION_JSON)), buildingController::updateBuilding)
                .andRoute(DELETE(BUILDING_DETAILS_URL).and(accept(APPLICATION_JSON)), buildingController::deleteBuilding);
    }
}
