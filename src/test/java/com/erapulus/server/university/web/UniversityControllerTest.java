package com.erapulus.server.university.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.university.dto.UniversityListDto;
import com.erapulus.server.university.dto.UniversityRequestDto;
import com.erapulus.server.university.dto.UniversityResponseDto;
import com.erapulus.server.university.service.UniversityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
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

@WebFluxTest(controllers = {UniversityRouter.class, UniversityController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class UniversityControllerTest {

    private final static int UNIVERSITY_ID_1 = 1;
    private final static int UNIVERSITY_ID_2 = 2;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    UniversityService universityService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listUniversities_shouldReturnUniversities() {
        // given
        var universityList = List.of(createUniversityListDto(UNIVERSITY_ID_1), createUniversityListDto(UNIVERSITY_ID_2));
        String expectedPayload = """
                [
                   {
                      "id":1,
                      "name":"name",
                      "logoUrl":"logoUrl"
                   },
                   {
                      "id":2,
                      "name":"name",
                      "logoUrl":"logoUrl"
                   }
                ]""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(universityService.listUniversities()).thenReturn(Mono.just(universityList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listUniversities_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(universityService.listUniversities()).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversity_shouldReturnCreatedUniversityWhenDataCorrect() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        var universityResponseDto = createUniversityResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "address2":"address2",
                   "zipcode":"00-000",
                   "city":"city",
                   "country":"country",
                   "description":"description",
                   "websiteUrl":"websiteUrl",
                   "logoUrl":"logoUrl"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(universityService.createUniversity(any(UniversityRequestDto.class))).thenReturn(Mono.just(universityResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversity_shouldReturnBadRequestWhenMissingField() {
        // given
        var universityRequestDto = createUniversityRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(universityService.createUniversity(any(UniversityRequestDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(universityRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversity_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(universityService.createUniversity(any(UniversityRequestDto.class))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversity_shouldReturnConflictWhenUniversityDuplicated() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "university.conflict");
        when(universityService.createUniversity(any(UniversityRequestDto.class))).thenThrow(new DuplicateKeyException("university"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversity_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(universityService.createUniversity(any(UniversityRequestDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university")
                             .build())
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getUniversityById_shouldReturnUniversity() {
        // given
        var universityResponseDto = createUniversityResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "address2":"address2",
                   "zipcode":"00-000",
                   "city":"city",
                   "country":"country",
                   "description":"description",
                   "websiteUrl":"websiteUrl",
                   "logoUrl":"logoUrl"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(universityService.getUniversityById(UNIVERSITY_ID_1)).thenReturn(Mono.just(universityResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getUniversityById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "university.not.found");
        when(universityService.getUniversityById(UNIVERSITY_ID_1)).thenReturn(Mono.error(new NoSuchElementException("university")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getUniversityById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(universityService.getUniversityById(UNIVERSITY_ID_1)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversity_shouldReturnUpdatedUniversityWhenDataCorrect() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        var universityResponseDto = createUniversityResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "address2":"address2",
                   "zipcode":"00-000",
                   "city":"city",
                   "country":"country",
                   "description":"description",
                   "websiteUrl":"websiteUrl",
                   "logoUrl":"logoUrl"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(universityService.updateUniversity(any(UniversityRequestDto.class), eq(UNIVERSITY_ID_1)))
                .thenReturn(Mono.just(universityResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversity_shouldReturnBadRequestWhenMissingField() {
        // given
        var universityRequestDto = createUniversityRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(universityService.updateUniversity(any(UniversityRequestDto.class), eq(UNIVERSITY_ID_1)))
                .thenThrow(new ConstraintViolationException(validator.validate(universityRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversity_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(universityService.updateUniversity(any(UniversityRequestDto.class), eq(UNIVERSITY_ID_1)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversity_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "university.not.found");
        when(universityService.updateUniversity(any(UniversityRequestDto.class), eq(UNIVERSITY_ID_1)))
                .thenReturn(Mono.error(new NoSuchElementException("university")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversity_shouldReturnConflictWhenUniversityDuplicated() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "university.conflict");
        when(universityService.updateUniversity(any(UniversityRequestDto.class), eq(UNIVERSITY_ID_1)))
                .thenThrow(new DuplicateKeyException("university"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }


    @Test
    void updateUniversity_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var universityRequestDto = createUniversityRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(universityService.updateUniversity(any(UniversityRequestDto.class), eq(UNIVERSITY_ID_1)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(universityRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteUniversity_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(universityService.deleteUniversity(UNIVERSITY_ID_1)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteUniversity_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "university.not.found");
        when(universityService.deleteUniversity(UNIVERSITY_ID_1)).thenReturn(Mono.error(new NoSuchElementException("university")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteUniversity_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(universityService.deleteUniversity(UNIVERSITY_ID_1)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}")
                             .build(UNIVERSITY_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversityLogo_shouldUpdateUniversityLogoWhenDataCorrect() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        var universityResponseDto = createUniversityResponseDto();
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "address":"address",
                   "address2":"address2",
                   "zipcode":"00-000",
                   "city":"city",
                   "country":"country",
                   "description":"description",
                   "websiteUrl":"websiteUrl",
                   "logoUrl":"logoUrl"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(universityService.updateUniversityLogo(eq(UNIVERSITY_ID_1), any(FilePart.class)))
                .thenReturn(Mono.just(universityResponseDto));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/logo")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversityLogo_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(universityService.updateUniversityLogo(eq(UNIVERSITY_ID_1), any(FilePart.class)))
                .thenThrow(new IllegalArgumentException());

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/logo")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversityLogo_shouldReturnBadRequestWhenWrongBodyFound() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("wrong_field", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(universityService.updateUniversityLogo(eq(UNIVERSITY_ID_1), any(FilePart.class)))
                .thenThrow(new IllegalArgumentException());

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/logo")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversityLogo_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "university.not.found");
        when(universityService.updateUniversityLogo(eq(UNIVERSITY_ID_1), any(FilePart.class)))
                .thenReturn(Mono.error(new NoSuchElementException("university")));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/logo")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateUniversityLogo_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("example_file.txt")).contentType(MediaType.MULTIPART_FORM_DATA);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(universityService.updateUniversityLogo(eq(UNIVERSITY_ID_1), any(FilePart.class)))
                .thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.patch()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/logo")
                             .build(UNIVERSITY_ID_1))
                     .contentType(MediaType.MULTIPART_FORM_DATA)
                     .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private UniversityListDto createUniversityListDto(int id) {
        return UniversityListDto.builder()
                                .id(id)
                                .name("name")
                                .logoUrl("logoUrl")
                                .build();
    }

    private UniversityResponseDto createUniversityResponseDto() {
        return UniversityResponseDto.builder()
                                    .id(UNIVERSITY_ID_1)
                                    .name("name")
                                    .address("address")
                                    .address2("address2")
                                    .city("city")
                                    .country("country")
                                    .zipcode("00-000")
                                    .description("description")
                                    .websiteUrl("websiteUrl")
                                    .logoUrl("logoUrl")
                                    .build();
    }

    private UniversityRequestDto createUniversityRequestDto() {
        return UniversityRequestDto.builder()
                                   .name("name")
                                   .address("address")
                                   .address2("address2")
                                   .city("city")
                                   .country("country")
                                   .zipcode("00-000")
                                   .description("description")
                                   .websiteUrl("websiteUrl")
                                   .build();
    }
}

