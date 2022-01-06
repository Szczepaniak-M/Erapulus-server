package com.erapulus.server.document.web;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.common.web.CommonRequestVariable.DOCUMENT_PATH_PARAM;
import static com.erapulus.server.common.web.CommonRequestVariable.UNIVERSITY_PATH_PARAM;
import static com.erapulus.server.common.web.OpenApiConstants.DOCUMENT_UNIVERSITY_BASE_URL_OPENAPI;
import static com.erapulus.server.common.web.OpenApiConstants.DOCUMENT_UNIVERSITY_DETAILS_URL_OPENAPI;
import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class DocumentUniversityRouter {

    public static final String DOCUMENT_UNIVERSITY_BASE_URL = format("/api/university/{%s}/document", UNIVERSITY_PATH_PARAM);
    public static final String DOCUMENT_UNIVERSITY_DETAILS_URL = format("/api/university/{%s}/document/{%s}", UNIVERSITY_PATH_PARAM, DOCUMENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = DOCUMENT_UNIVERSITY_BASE_URL_OPENAPI, method = GET, beanClass = DocumentUniversityController.class, beanMethod = "listDocumentsForUniversity"),
            @RouterOperation(path = DOCUMENT_UNIVERSITY_BASE_URL_OPENAPI, method = POST, beanClass = DocumentUniversityController.class, beanMethod = "uploadDocumentForUniversity"),
            @RouterOperation(path = DOCUMENT_UNIVERSITY_DETAILS_URL_OPENAPI, method = GET, beanClass = DocumentUniversityController.class, beanMethod = "getDocumentForUniversityById"),
            @RouterOperation(path = DOCUMENT_UNIVERSITY_DETAILS_URL_OPENAPI, method = PUT, beanClass = DocumentUniversityController.class, beanMethod = "updateDocumentForUniversity"),
            @RouterOperation(path = DOCUMENT_UNIVERSITY_DETAILS_URL_OPENAPI, method = DELETE, beanClass = DocumentUniversityController.class, beanMethod = "deleteDocumentForUniversity"),
    })
    @Bean
    RouterFunction<ServerResponse> documentUniversityRoutes(DocumentUniversityController documentUniversityController) {
        return route(GET(DOCUMENT_UNIVERSITY_BASE_URL).and(accept(APPLICATION_JSON)), documentUniversityController::listDocumentsForUniversity)
                .andRoute(POST(DOCUMENT_UNIVERSITY_BASE_URL).and(contentType(MULTIPART_FORM_DATA)), documentUniversityController::uploadDocumentForUniversity)
                .andRoute(GET(DOCUMENT_UNIVERSITY_DETAILS_URL).and(accept(APPLICATION_JSON)), documentUniversityController::getDocumentForUniversityById)
                .andRoute(PUT(DOCUMENT_UNIVERSITY_DETAILS_URL).and(contentType(APPLICATION_JSON)), documentUniversityController::updateDocumentForUniversity)
                .andRoute(RequestPredicates.DELETE(DOCUMENT_UNIVERSITY_DETAILS_URL).and(accept(APPLICATION_JSON)), documentUniversityController::deleteDocumentForUniversity);
    }
}
