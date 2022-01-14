package com.erapulus.server.module.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.module.dto.ModuleRequestDto;
import com.erapulus.server.module.dto.ModuleResponseDto;
import com.erapulus.server.module.service.ModuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {ModuleRouter.class, ModuleController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class ModuleControllerTest {

    private final static int MODULE_ID_1 = 1;
    private final static int MODULE_ID_2 = 2;
    private final static int PROGRAM_ID = 3;
    private final static int FACULTY_ID = 4;
    private final static int UNIVERSITY_ID = 5;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    ModuleService moduleService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listModules_shouldReturnModules() {
        // given
        var moduleList = createPageableModuleList();
        String expectedPayload = """
                {
                   "content":[
                     {
                       "id":1,
                       "name":"name",
                       "abbrev":"abbrev",
                       "description":"description",
                       "programId":3
                     },
                     {
                       "id":2,
                       "name":"name",
                       "abbrev":"abbrev",
                       "description":"description",
                       "programId":3
                     }
                   ],
                   "currentPage":1,
                   "totalCount":12,
                   "pageSize":10,
                   "offset":10
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(moduleService.listModules(eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.just(moduleList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listModules_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(moduleService.listModules(eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listModules_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(moduleService.listModules(eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createModule_shouldReturnCreatedModuleWhenDataCorrect() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        var moduleResponseDto = createModuleResponseDto(MODULE_ID_1);
        String expectedPayload = """
                {
                  "id":1,
                  "name":"name",
                  "abbrev":"abbrev",
                  "description":"description",
                  "programId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(moduleService.createModule(any(ModuleRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID))).thenReturn(Mono.just(moduleResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createModule_shouldReturnBadRequestWhenMissingField() {
        // given
        var moduleRequestDto = createModuleRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(moduleService.createModule(any(ModuleRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(moduleRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createModule_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(moduleService.createModule(any(ModuleRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }
    @Test
    void createModule_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(moduleService.createModule(any(ModuleRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createModule_shouldReturnConflictWhenModuleDuplicated() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "module.conflict");
        when(moduleService.createModule(any(ModuleRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID))).thenThrow(new DuplicateKeyException("module"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createModule_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(moduleService.createModule(any(ModuleRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getModuleById_shouldReturnModule() {
        // given
        var moduleResponseDto = createModuleResponseDto(MODULE_ID_1);
        String expectedPayload = """
                {
                  "id":1,
                  "name":"name",
                  "abbrev":"abbrev",
                  "description":"description",
                  "programId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(moduleService.getModuleById(MODULE_ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID)).thenReturn(Mono.just(moduleResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getModuleById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "module.not.found");
        when(moduleService.getModuleById(MODULE_ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID)).thenReturn(Mono.error(new NoSuchElementException("module")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getModuleById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(moduleService.getModuleById(MODULE_ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateModule_shouldReturnUpdatedModuleWhenDataCorrect() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        var moduleResponseDto = createModuleResponseDto(MODULE_ID_1);
        String expectedPayload = """
                {
                    "id":1,
                    "name":"name",
                    "abbrev":"abbrev",
                    "description":"description",
                    "programId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(moduleService.updateModule(any(ModuleRequestDto.class), eq(MODULE_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenReturn(Mono.just(moduleResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateModule_shouldReturnBadRequestWhenMissingField() {
        // given
        var moduleRequestDto = createModuleRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(moduleService.updateModule(any(ModuleRequestDto.class), eq(MODULE_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(moduleRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateModule_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(moduleService.updateModule(any(ModuleRequestDto.class), eq(MODULE_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateModule_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "module.not.found");
        when(moduleService.updateModule(any(ModuleRequestDto.class), eq(MODULE_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("module")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateModule_shouldReturnConflictWhenModuleDuplicated() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "module.conflict");
        when(moduleService.updateModule(any(ModuleRequestDto.class), eq(MODULE_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenThrow(new DuplicateKeyException("module"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateModule_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var moduleRequestDto = createModuleRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(moduleService.updateModule(any(ModuleRequestDto.class), eq(MODULE_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID), eq(PROGRAM_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(moduleRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteModule_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(moduleService.deleteModule(MODULE_ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteModule_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "module.not.found");
        when(moduleService.deleteModule(MODULE_ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID)).thenReturn(Mono.error(new NoSuchElementException("module")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteModule_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(moduleService.deleteModule(MODULE_ID_1, UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}/module/{moduleId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID, MODULE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private PageablePayload<ModuleResponseDto> createPageableModuleList() {
        return new PageablePayload<>(List.of(createModuleResponseDto(MODULE_ID_1), createModuleResponseDto(MODULE_ID_2)),
                PageRequest.of(1, 10), 12);
    }

    private ModuleResponseDto createModuleResponseDto(int id) {
        return ModuleResponseDto.builder()
                                 .id(id)
                                 .name("name")
                                 .abbrev("abbrev")
                                 .description("description")
                                 .programId(PROGRAM_ID)
                                 .build();
    }

    private ModuleRequestDto createModuleRequestDto() {
        return ModuleRequestDto.builder()
                                .name("name")
                                .abbrev("abbrev")
                                .description("description")
                                .build();
    }
}

