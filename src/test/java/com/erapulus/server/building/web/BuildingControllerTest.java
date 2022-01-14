package com.erapulus.server.building.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.building.dto.BuildingRequestDto;
import com.erapulus.server.building.dto.BuildingResponseDto;
import com.erapulus.server.building.service.BuildingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DuplicateKeyException;
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

@WebFluxTest(controllers = {BuildingRouter.class, BuildingController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class BuildingControllerTest {

    private final static int BUILDING_ID_1 = 1;
    private final static int BUILDING_ID_2 = 2;
    private final static int UNIVERSITY_ID = 3;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    BuildingService buildingService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listBuildings_shouldReturnBuildings() {
        // given
        var buildingList = List.of(createBuildingResponseDto(BUILDING_ID_1), createBuildingResponseDto(BUILDING_ID_2));
        String expectedPayload = """
                [
                    {
                      "id":1,
                      "name":"name",
                      "abbrev":"abbrev",
                      "latitude":10.0,
                      "longitude":10.0,
                      "universityId":3
                    },
                    {
                      "id":2,
                      "name":"name",
                      "abbrev":"abbrev",
                      "latitude":10.0,
                      "longitude":10.0,
                      "universityId":3
                    }
                ]""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(buildingService.listBuildings(UNIVERSITY_ID)).thenReturn(Mono.just(buildingList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listBuildings_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(buildingService.listBuildings(UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createBuilding_shouldReturnCreatedBuildingWhenDataCorrect() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        var buildingResponseDto = createBuildingResponseDto(BUILDING_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "abbrev":"abbrev",
                   "latitude":10.0,
                   "longitude":10.0,
                   "universityId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(buildingService.createBuilding(any(BuildingRequestDto.class), eq(UNIVERSITY_ID))).thenReturn(Mono.just(buildingResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createBuilding_shouldReturnBadRequestWhenMissingField() {
        // given
        var buildingRequestDto = createBuildingRequestDto().latitude(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;latitude.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(buildingService.createBuilding(any(BuildingRequestDto.class), eq(UNIVERSITY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(buildingRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createBuilding_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(buildingService.createBuilding(any(BuildingRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createBuilding_shouldReturnConflictWhenBuildingDuplicated() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "building.conflict");
        when(buildingService.createBuilding(any(BuildingRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new DuplicateKeyException("building"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createBuilding_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(buildingService.createBuilding(any(BuildingRequestDto.class), eq(UNIVERSITY_ID))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building")
                             .build(UNIVERSITY_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getBuildingById_shouldReturnBuilding() {
        // given
        var buildingResponseDto = createBuildingResponseDto(BUILDING_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "abbrev":"abbrev",
                   "latitude":10.0,
                   "longitude":10.0,
                   "universityId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(buildingService.getBuildingById(BUILDING_ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(buildingResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getBuildingById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "building.not.found");
        when(buildingService.getBuildingById(BUILDING_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new NoSuchElementException("building")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getBuildingById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(buildingService.getBuildingById(BUILDING_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateBuilding_shouldReturnUpdatedBuildingWhenDataCorrect() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        var buildingResponseDto = createBuildingResponseDto(BUILDING_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "name":"name",
                   "abbrev":"abbrev",
                   "latitude":10.0,
                   "longitude":10.0,
                   "universityId":3
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(buildingService.updateBuilding(any(BuildingRequestDto.class), eq(BUILDING_ID_1), eq(UNIVERSITY_ID)))
                .thenReturn(Mono.just(buildingResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateBuilding_shouldReturnBadRequestWhenMissingField() {
        // given
        var buildingRequestDto = createBuildingRequestDto().latitude(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;latitude.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(buildingService.updateBuilding(any(BuildingRequestDto.class), eq(BUILDING_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(buildingRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateBuilding_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(buildingService.updateBuilding(any(BuildingRequestDto.class), eq(BUILDING_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateBuilding_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "building.not.found");
        when(buildingService.updateBuilding(any(BuildingRequestDto.class), eq(BUILDING_ID_1), eq(UNIVERSITY_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("building")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateBuilding_shouldReturnConflictWhenBuildingDuplicated() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "building.conflict");
        when(buildingService.updateBuilding(any(BuildingRequestDto.class), eq(BUILDING_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new DuplicateKeyException("building"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }


    @Test
    void updateBuilding_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var buildingRequestDto = createBuildingRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(buildingService.updateBuilding(any(BuildingRequestDto.class), eq(BUILDING_ID_1), eq(UNIVERSITY_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(buildingRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteBuilding_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(buildingService.deleteBuilding(BUILDING_ID_1, UNIVERSITY_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteBuilding_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "building.not.found");
        when(buildingService.deleteBuilding(BUILDING_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new NoSuchElementException("building")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteBuilding_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(buildingService.deleteBuilding(BUILDING_ID_1, UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/building/{buildingId}")
                             .build(UNIVERSITY_ID, BUILDING_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private BuildingResponseDto createBuildingResponseDto(int id) {
        return BuildingResponseDto.builder()
                                  .id(id)
                                  .name("name")
                                  .abbrev("abbrev")
                                  .latitude(10.0)
                                  .longitude(10.0)
                                  .universityId(UNIVERSITY_ID)
                                  .build();
    }

    private BuildingRequestDto createBuildingRequestDto() {
        return BuildingRequestDto.builder()
                                  .name("name")
                                  .abbrev("abbrev")
                                  .latitude(10.0)
                                  .longitude(10.0)
                                  .build();
    }
}

