package com.erapulus.server.program.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.common.web.PageablePayload;
import com.erapulus.server.program.dto.ProgramRequestDto;
import com.erapulus.server.program.dto.ProgramResponseDto;
import com.erapulus.server.program.service.ProgramService;
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

@WebFluxTest(controllers = {ProgramRouter.class, ProgramController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class ProgramControllerTest {

    private final static int PROGRAM_ID_1 = 1;
    private final static int PROGRAM_ID_2 = 2;
    private final static int FACULTY_ID = 3;
    private final static int UNIVERSITY_ID = 4;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    ProgramService programService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listPrograms_shouldReturnPrograms() {
        // given
        var programList = createPageableProgramList();
        String expectedPayload = """
                {
                   "content":[
                     {
                       "id":1,
                       "name":"name",
                       "abbrev":"abbrev",
                       "description":"description",
                       "facultyId":3
                     },
                     {
                       "id":2,
                       "name":"name",
                       "abbrev":"abbrev",
                       "description":"description",
                       "facultyId":3
                     }
                   ],
                   "currentPage":1,
                   "totalCount":12,
                   "pageSize":10,
                   "offset":10
                 }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(programService.listPrograms(eq(UNIVERSITY_ID), eq(FACULTY_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.just(programList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listPrograms_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(programService.listPrograms(eq(UNIVERSITY_ID), eq(FACULTY_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listPrograms_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(programService.listPrograms(eq(UNIVERSITY_ID), eq(FACULTY_ID), eq("name"), any(PageRequest.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .queryParam("name", "name")
                             .queryParam("page", "1")
                             .queryParam("pageSize", "10")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createProgram_shouldReturnCreatedProgramWhenDataCorrect() {
        // given
        var programRequestDto = createProgramRequestDto();
        var programResponseDto = createProgramResponseDto(PROGRAM_ID_1);
        String expectedPayload = """
                {
                  "id":1,
                  "name":"name",
                  "abbrev":"abbrev",
                  "description":"description",
                  "facultyId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(programService.createProgram(any(ProgramRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID))).thenReturn(Mono.just(programResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createProgram_shouldReturnBadRequestWhenMissingField() {
        // given
        var programRequestDto = createProgramRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(programService.createProgram(any(ProgramRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(programRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createProgram_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(programService.createProgram(any(ProgramRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }
    @Test
    void createProgram_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var programRequestDto = createProgramRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "faculty.not.found");
        when(programService.createProgram(any(ProgramRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("faculty")));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createProgram_shouldReturnConflictWhenProgramDuplicated() {
        // given
        var programRequestDto = createProgramRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "program.conflict");
        when(programService.createProgram(any(ProgramRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID))).thenThrow(new DuplicateKeyException("program"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createProgram_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var programRequestDto = createProgramRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(programService.createProgram(any(ProgramRequestDto.class), eq(UNIVERSITY_ID), eq(FACULTY_ID))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program")
                             .build(UNIVERSITY_ID, FACULTY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getProgramById_shouldReturnProgram() {
        // given
        var programResponseDto = createProgramResponseDto(PROGRAM_ID_1);
        String expectedPayload = """
                {
                  "id":1,
                  "name":"name",
                  "abbrev":"abbrev",
                  "description":"description",
                  "facultyId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(programService.getProgramById(PROGRAM_ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(programResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getProgramById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "program.not.found");
        when(programService.getProgramById(PROGRAM_ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.error(new NoSuchElementException("program")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getProgramById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(programService.getProgramById(PROGRAM_ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateProgram_shouldReturnUpdatedProgramWhenDataCorrect() {
        // given
        var programRequestDto = createProgramRequestDto();
        var programResponseDto = createProgramResponseDto(PROGRAM_ID_1);
        String expectedPayload = """
                {
                    "id":1,
                    "name":"name",
                    "abbrev":"abbrev",
                    "description":"description",
                    "facultyId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(programService.updateProgram(any(ProgramRequestDto.class), eq(PROGRAM_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenReturn(Mono.just(programResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateProgram_shouldReturnBadRequestWhenMissingField() {
        // given
        var programRequestDto = createProgramRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(programService.updateProgram(any(ProgramRequestDto.class), eq(PROGRAM_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(programRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateProgram_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(programService.updateProgram(any(ProgramRequestDto.class), eq(PROGRAM_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateProgram_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var programRequestDto = createProgramRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "program.not.found");
        when(programService.updateProgram(any(ProgramRequestDto.class), eq(PROGRAM_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("program")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateProgram_shouldReturnConflictWhenProgramDuplicated() {
        // given
        var programRequestDto = createProgramRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "program.conflict");
        when(programService.updateProgram(any(ProgramRequestDto.class), eq(PROGRAM_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenThrow(new DuplicateKeyException("program"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateProgram_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var programRequestDto = createProgramRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(programService.updateProgram(any(ProgramRequestDto.class), eq(PROGRAM_ID_1), eq(UNIVERSITY_ID), eq(FACULTY_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(programRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteProgram_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(programService.deleteProgram(PROGRAM_ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteProgram_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "program.not.found");
        when(programService.deleteProgram(PROGRAM_ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.error(new NoSuchElementException("program")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteProgram_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(programService.deleteProgram(PROGRAM_ID_1, UNIVERSITY_ID, FACULTY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/faculty/{facultyId}/program/{programId}")
                             .build(UNIVERSITY_ID, FACULTY_ID, PROGRAM_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private PageablePayload<ProgramResponseDto> createPageableProgramList() {
        return new PageablePayload<>(List.of(createProgramResponseDto(PROGRAM_ID_1), createProgramResponseDto(PROGRAM_ID_2)),
                PageRequest.of(1, 10), 12);
    }

    private ProgramResponseDto createProgramResponseDto(int id) {
        return ProgramResponseDto.builder()
                                 .id(id)
                                 .name("name")
                                 .abbrev("abbrev")
                                 .description("description")
                                 .facultyId(FACULTY_ID)
                                 .build();
    }

    private ProgramRequestDto createProgramRequestDto() {
        return ProgramRequestDto.builder()
                                .name("name")
                                .abbrev("abbrev")
                                .description("description")
                                .build();
    }
}

