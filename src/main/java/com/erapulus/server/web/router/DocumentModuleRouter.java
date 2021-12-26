package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.DocumentModuleController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.*;
import static com.erapulus.server.web.common.OpenApiConstants.DOCUMENT_MODULE_BASE_URL_OPENAPI;
import static com.erapulus.server.web.common.OpenApiConstants.DOCUMENT_MODULE_DETAILS_URL_OPENAPI;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
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
public class DocumentModuleRouter {

    public static final String DOCUMENT_MODULE_BASE_URL = format("/api/university/{%s}/faculty/{%s}/program/{%s}/module/{%s}/document", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM, MODULE_PATH_PARAM);
    public static final String DOCUMENT_MODULE_DETAILS_URL = format("/api/university/{%s}/faculty/{%s}/program/{%s}/module/{%s}/document/{%s}", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM, MODULE_PATH_PARAM, DOCUMENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = DOCUMENT_MODULE_BASE_URL_OPENAPI, method = GET, beanClass = DocumentModuleController.class, beanMethod = "listDocumentsForModule"),
            @RouterOperation(path = DOCUMENT_MODULE_BASE_URL_OPENAPI, method = POST, beanClass = DocumentModuleController.class, beanMethod = "uploadDocumentForModule"),
            @RouterOperation(path = DOCUMENT_MODULE_DETAILS_URL_OPENAPI, method = GET, beanClass = DocumentModuleController.class, beanMethod = "getDocumentForModuleById"),
            @RouterOperation(path = DOCUMENT_MODULE_DETAILS_URL_OPENAPI, method = PUT, beanClass = DocumentModuleController.class, beanMethod = "updateDocumentForModule"),
            @RouterOperation(path = DOCUMENT_MODULE_DETAILS_URL_OPENAPI, method = DELETE, beanClass = DocumentModuleController.class, beanMethod = "deleteDocumentForModule")
    })
    @Bean
    RouterFunction<ServerResponse> documentModuleRoutes(DocumentModuleController documentModuleController) {
        return route(GET(DOCUMENT_MODULE_BASE_URL).and(accept(APPLICATION_JSON)), documentModuleController::listDocumentsForModule)
                .andRoute(POST(DOCUMENT_MODULE_BASE_URL).and(contentType(MULTIPART_FORM_DATA)), documentModuleController::uploadDocumentForModule)
                .andRoute(GET(DOCUMENT_MODULE_DETAILS_URL).and(accept(APPLICATION_JSON)), documentModuleController::getDocumentForModuleById)
                .andRoute(PUT(DOCUMENT_MODULE_DETAILS_URL).and(contentType(APPLICATION_JSON)), documentModuleController::updateDocumentForModule)
                .andRoute(DELETE(DOCUMENT_MODULE_DETAILS_URL).and(accept(APPLICATION_JSON)), documentModuleController::deleteDocumentForModule);
    }
}
