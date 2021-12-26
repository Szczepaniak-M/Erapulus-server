package com.erapulus.server.web.router;

import com.erapulus.server.web.controller.DocumentController;
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
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class DocumentRouter {

    public static final String DOCUMENT_UNIVERSITY_BASE_URL = format("/api/university/{%s}/document", UNIVERSITY_PATH_PARAM);
    public static final String DOCUMENT_PROGRAM_BASE_URL = format("/api/university/{%s}/faculty/{%s}/program/{%s}/document", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM);
    public static final String DOCUMENT_MODULE_BASE_URL = format("/api/university/{%s}/faculty/{%s}/program/{%s}/module/{%s}/document", UNIVERSITY_PATH_PARAM, FACULTY_PATH_PARAM, PROGRAM_PATH_PARAM, MODULE_PATH_PARAM);

    @RouterOperations({
            @RouterOperation(path = DOCUMENT_UNIVERSITY_BASE_URL_OPENAPI, method = GET, beanClass = DocumentController.class, beanMethod = "listFilesForUniversity"),
            @RouterOperation(path = DOCUMENT_PROGRAM_BASE_URL_OPENAPI, method = GET, beanClass = DocumentController.class, beanMethod = "listFilesForProgram"),
            @RouterOperation(path = DOCUMENT_MODULE_BASE_URL_OPENAPI, method = GET, beanClass = DocumentController.class, beanMethod = "listFilesForModule"),
            @RouterOperation(path = DOCUMENT_UNIVERSITY_BASE_URL_OPENAPI, method = POST, beanClass = DocumentController.class, beanMethod = "uploadFileForUniversity"),
            @RouterOperation(path = DOCUMENT_PROGRAM_BASE_URL_OPENAPI, method = POST, beanClass = DocumentController.class, beanMethod = "uploadFileForProgram"),
            @RouterOperation(path = DOCUMENT_MODULE_BASE_URL_OPENAPI, method = POST, beanClass = DocumentController.class, beanMethod = "uploadFileForModule"),

    })
    @Bean
    RouterFunction<ServerResponse> documentRoutes(DocumentController documentController) {
        return route(GET(DOCUMENT_UNIVERSITY_BASE_URL).and(accept(APPLICATION_JSON)), documentController::listFilesForUniversity)
                .andRoute(GET(DOCUMENT_PROGRAM_BASE_URL).and(accept(APPLICATION_JSON)), documentController::listFilesForProgram)
                .andRoute(GET(DOCUMENT_MODULE_BASE_URL).and(accept(APPLICATION_JSON)), documentController::listFilesForModule)
                .andRoute(POST(DOCUMENT_UNIVERSITY_BASE_URL).and(contentType(MULTIPART_FORM_DATA)), documentController::uploadFileForUniversity)
                .andRoute(POST(DOCUMENT_PROGRAM_BASE_URL).and(contentType(MULTIPART_FORM_DATA)), documentController::uploadFileForProgram)
                .andRoute(POST(DOCUMENT_MODULE_BASE_URL).and(contentType(MULTIPART_FORM_DATA)), documentController::uploadFileForModule);
    }
}
