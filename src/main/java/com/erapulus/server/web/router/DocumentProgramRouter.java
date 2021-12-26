package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.DocumentProgramController;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.erapulus.server.web.common.CommonRequestVariable.*;
import static com.erapulus.server.web.common.OpenApiConstants.DOCUMENT_PROGRAM_BASE_URL_OPENAPI;
import static com.erapulus.server.web.common.OpenApiConstants.DOCUMENT_PROGRAM_DETAILS_URL_OPENAPI;
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
public class DocumentProgramRouter {
    public static final String DOCUMENT_PROGRAM_BASE_URL = format("/api/university/{%s}/faculty/{%s}/program/{%s}/document", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM);
    public static final String DOCUMENT_PROGRAM_DETAILS_URL = format("/api/university/{%s}/faculty/{%s}/program/{%s}/document/{%s}", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM, DOCUMENT_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = DOCUMENT_PROGRAM_BASE_URL_OPENAPI, method = GET, beanClass = DocumentProgramController.class, beanMethod = "listDocumentsForProgram"),
            @RouterOperation(path = DOCUMENT_PROGRAM_BASE_URL_OPENAPI, method = POST, beanClass = DocumentProgramController.class, beanMethod = "uploadDocumentForProgram"),
            @RouterOperation(path = DOCUMENT_PROGRAM_DETAILS_URL_OPENAPI, method = GET, beanClass = DocumentProgramController.class, beanMethod = "getDocumentForProgramById"),
            @RouterOperation(path = DOCUMENT_PROGRAM_DETAILS_URL_OPENAPI, method = PUT, beanClass = DocumentProgramController.class, beanMethod = "updateDocumentForProgram"),
            @RouterOperation(path = DOCUMENT_PROGRAM_DETAILS_URL_OPENAPI, method = DELETE, beanClass = DocumentProgramController.class, beanMethod = "deleteDocumentForProgram"),
    })
    @Bean
    RouterFunction<ServerResponse> documentProgramRoutes(DocumentProgramController documentProgramController) {
        return route(GET(DOCUMENT_PROGRAM_BASE_URL).and(accept(APPLICATION_JSON)), documentProgramController::listDocumentsForProgram)
                .andRoute(POST(DOCUMENT_PROGRAM_BASE_URL).and(contentType(MULTIPART_FORM_DATA)), documentProgramController::uploadDocumentForProgram)
                .andRoute(GET(DOCUMENT_PROGRAM_DETAILS_URL).and(accept(APPLICATION_JSON)), documentProgramController::getDocumentForProgramById)
                .andRoute(PUT(DOCUMENT_PROGRAM_DETAILS_URL).and(contentType(APPLICATION_JSON)), documentProgramController::updateDocumentForProgram)
                .andRoute(RequestPredicates.DELETE(DOCUMENT_PROGRAM_DETAILS_URL).and(accept(APPLICATION_JSON)), documentProgramController::deleteDocumentForProgram);
    }
}
