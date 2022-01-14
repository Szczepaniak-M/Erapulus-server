package com.erapulus.server.faculty.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.applicationuser.dto.ApplicationUserDto;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.faculty.dto.FacultyRequestDto;
import com.erapulus.server.faculty.dto.FacultyResponseDto;
import com.erapulus.server.faculty.service.FacultyService;
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

@WebFluxTest(controllers = {FacultyRouter.class, FacultyController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class FacultyControllerTest {

    private final static int FACULTY_ID_1 = 1;
    private final static int FACULTY_ID_2 = 2;
    private final static int UNIVERSITY_ID = 3;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    FacultyService facultyService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listFaculties_shouldReturnFaculties() {
        // given
        var facultyList = createPageableFacultyList();
        String expectedPayload = """
                {
                   "content":[
                     {
                       "id":1,
                       "name":"name",
                       "address":"address",
                       "email":"example@gmail.com",
                       "universityId":3
                     },
                     {
                       "id":2,
                       "name":"name",
                       "address":"address",
                       "email":"example@gmail.com",
                       "universityId":3
                     }
                   ],
                   "currentPage":1,
                   "totalCount":12,
                   "pageSize":10,
                   "offset":10
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(facultyService.listFaculties(eq(UNIVERSITY_ID), eq("name"), any(PageRequest.class))).thenReturn(Mono.just(facultyList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listFaculties_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(facultyService.listFaculties(eq(UNIVERSITY_ID), eq("name"), any(PageRequest.class))).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createFaculty_shouldReturnCreatedFacultyWhenDataCorrect() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        var facultyResponseDto = createFacultyResponseDto(FACULTY_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "email":"example@gmail.com",
                   "universityId":3
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(facultyService.createFaculty(any(FacultyRequestDto.class), eq(UNIVERSITY_ID))).thenReturn(Mono.just(facultyResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createFaculty_shouldReturnBadRequestWhenMissingField() {
        // given
        var facultyRequestDto = createFacultyRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(facultyService.createFaculty(any(FacultyRequestDto.class), eq(UNIVERSITY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(facultyRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createFaculty_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(facultyService.createFaculty(any(FacultyRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createFaculty_shouldReturnConflictWhenFacultyDuplicated() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "faculty.conflict");
        when(facultyService.createFaculty(any(FacultyRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new DuplicateKeyException("faculty"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createFaculty_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(facultyService.createFaculty(any(FacultyRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getFacultyById_shouldReturnFaculty() {
        // given
        var facultyResponseDto = createFacultyResponseDto(FACULTY_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "email":"example@gmail.com",
                   "universityId":3
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(facultyService.getFacultyById(FACULTY_ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(facultyResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getFacultyById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(facultyService.getFacultyById(FACULTY_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getFacultyById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(facultyService.getFacultyById(FACULTY_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFacultyWhenDataCorrect() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        var facultyResponseDto = createFacultyResponseDto(FACULTY_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "email":"example@gmail.com",
                   "universityId":3
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(facultyService.updateFaculty(any(FacultyRequestDto.class), eq(FACULTY_ID_1), eq(UNIVERSITY_ID)))
                .thenReturn(Mono.just(facultyResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateFaculty_shouldReturnBadRequestWhenMissingField() {
        // given
        var facultyRequestDto = createFacultyRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(facultyService.updateFaculty(any(FacultyRequestDto.class), eq(FACULTY_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(facultyRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateFaculty_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(facultyService.updateFaculty(any(FacultyRequestDto.class), eq(FACULTY_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateFaculty_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(facultyService.updateFaculty(any(FacultyRequestDto.class), eq(FACULTY_ID_1), eq(UNIVERSITY_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateFaculty_shouldReturnConflictWhenFacultyDuplicated() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "faculty.conflict");
        when(facultyService.updateFaculty(any(FacultyRequestDto.class), eq(FACULTY_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new DuplicateKeyException("faculty"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }


    @Test
    void updateFaculty_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var facultyRequestDto = createFacultyRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(facultyService.updateFaculty(any(FacultyRequestDto.class), eq(FACULTY_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(facultyRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteFaculty_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(facultyService.deleteFaculty(FACULTY_ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteFaculty_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(facultyService.deleteFaculty(FACULTY_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteFaculty_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(facultyService.deleteFaculty(FACULTY_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}")
                             .build(UNIVERSITY_ID, FACULTY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private PageablePayload<FacultyResponseDto> createPageableFacultyList() {
        return new PageablePayload<>(List.of(createFacultyResponseDto(FACULTY_ID_1), createFacultyResponseDto(FACULTY_ID_2)),
                PageRequest.of(1, 10), 12);
    }

    private FacultyResponseDto createFacultyResponseDto(int id) {
        return FacultyResponseDto.builder()
                                 .id(id)
                                 .name("name")
                                 .email("example@gmail.com")
                                 .address("address")
                                 .universityId(UNIVERSITY_ID)
                                 .build();
    }

    private FacultyRequestDto createFacultyRequestDto() {
        return FacultyRequestDto.builder()
                                .name("name")
                                .email("example@gmail.com")
                                .address("address")
                                .build();
    }
}

