package com.erapulus.server.device.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.device.dto.DeviceRequestDto;
import com.erapulus.server.device.dto.DeviceResponseDto;
import com.erapulus.server.device.service.DeviceService;
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

@WebFluxTest(controllers = {DeviceRouter.class, DeviceController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class DeviceControllerTest {

    private final static int DEVICE_ID_1 = 1;
    private final static int DEVICE_ID_2 = 2;
    private final static int STUDENT_ID = 3;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    DeviceService deviceService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void listDevices_shouldReturnDevices() {
        // given
        var deviceList = List.of(createDeviceResponseDto(DEVICE_ID_1), createDeviceResponseDto(DEVICE_ID_2));
        String expectedPayload = """
                [
                  {
                     "id":1,
                     "applicationUserId":3,
                     "deviceId":"deviceId",
                     "name":"name"
                  },
                  {
                    "id":2,
                    "applicationUserId":3,
                    "deviceId":"deviceId",
                    "name":"name"
                  }
                ]""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(deviceService.listDevices(STUDENT_ID)).thenReturn(Mono.just(deviceList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listDevices_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(deviceService.listDevices(STUDENT_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createDevice_shouldReturnCreatedDeviceWhenDataCorrect() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        var deviceResponseDto = createDeviceResponseDto(DEVICE_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "applicationUserId":3,
                   "deviceId":"deviceId",
                   "name":"name"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(deviceService.createDevice(any(DeviceRequestDto.class), eq(STUDENT_ID))).thenReturn(Mono.just(deviceResponseDto));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createDevice_shouldReturnBadRequestWhenMissingField() {
        // given
        var deviceRequestDto = createDeviceRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(deviceService.createDevice(any(DeviceRequestDto.class), eq(STUDENT_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(deviceRequestDto)));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createDevice_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(deviceService.createDevice(any(DeviceRequestDto.class), eq(STUDENT_ID))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createDevice_shouldReturnConflictWhenDeviceDuplicated() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "device.conflict");
        when(deviceService.createDevice(any(DeviceRequestDto.class), eq(STUDENT_ID))).thenThrow(new DuplicateKeyException("device"));

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createDevice_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(deviceService.createDevice(any(DeviceRequestDto.class), eq(STUDENT_ID))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device")
                             .build(STUDENT_ID))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getDeviceById_shouldReturnDevice() {
        // given
        var deviceResponseDto = createDeviceResponseDto(DEVICE_ID_1);
        String expectedPayload = """
                {
                  "id":1,
                  "applicationUserId":3,
                  "deviceId":"deviceId",
                  "name":"name"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(deviceService.getDeviceById(DEVICE_ID_1, STUDENT_ID)).thenReturn(Mono.just(deviceResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getDeviceById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "device.not.found");
        when(deviceService.getDeviceById(DEVICE_ID_1, STUDENT_ID)).thenReturn(Mono.error(new NoSuchElementException("device")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getDeviceById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(deviceService.getDeviceById(DEVICE_ID_1, STUDENT_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateDevice_shouldReturnUpdatedDeviceWhenDataCorrect() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        var deviceResponseDto = createDeviceResponseDto(DEVICE_ID_1);
        String expectedPayload = """
                {
                  "id":1,
                  "applicationUserId":3,
                  "deviceId":"deviceId",
                  "name":"name"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(deviceService.updateDevice(any(DeviceRequestDto.class), eq(DEVICE_ID_1), eq(STUDENT_ID)))
                .thenReturn(Mono.just(deviceResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateDevice_shouldReturnBadRequestWhenMissingField() {
        // given
        var deviceRequestDto = createDeviceRequestDto().name(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;name.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(deviceService.updateDevice(any(DeviceRequestDto.class), eq(DEVICE_ID_1), eq(STUDENT_ID)))
                .thenThrow(new ConstraintViolationException(validator.validate(deviceRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateDevice_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(deviceService.updateDevice(any(DeviceRequestDto.class), eq(DEVICE_ID_1), eq(STUDENT_ID)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateDevice_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "device.not.found");
        when(deviceService.updateDevice(any(DeviceRequestDto.class), eq(DEVICE_ID_1), eq(STUDENT_ID)))
                .thenReturn(Mono.error(new NoSuchElementException("device")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateDevice_shouldReturnConflictWhenDeviceDuplicated() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "device.conflict");
        when(deviceService.updateDevice(any(DeviceRequestDto.class), eq(DEVICE_ID_1), eq(STUDENT_ID)))
                .thenThrow(new DuplicateKeyException("device"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateDevice_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var deviceRequestDto = createDeviceRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(deviceService.updateDevice(any(DeviceRequestDto.class), eq(DEVICE_ID_1), eq(STUDENT_ID)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(deviceRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteDevice_shouldReturnNoContent() {
        // given
        String expectedPayload = null;
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.NO_CONTENT.value(), expectedPayload);
        when(deviceService.deleteDevice(DEVICE_ID_1, STUDENT_ID)).thenReturn(Mono.just(true));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NO_CONTENT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteDevice_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "device.not.found");
        when(deviceService.deleteDevice(DEVICE_ID_1, STUDENT_ID)).thenReturn(Mono.error(new NoSuchElementException("device")));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void deleteDevice_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(deviceService.deleteDevice(DEVICE_ID_1, STUDENT_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.delete()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/student/{studentId}/device/{deviceId}")
                             .build(STUDENT_ID, DEVICE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private DeviceResponseDto createDeviceResponseDto(int id) {
        return DeviceResponseDto.builder()
                                .id(id)
                                .applicationUserId(STUDENT_ID)
                                .name("name")
                                .deviceId("deviceId")
                                .build();
    }

    private DeviceRequestDto createDeviceRequestDto() {
        return DeviceRequestDto.builder()
                               .name("name")
                               .deviceId("deviceId")
                               .build();
    }
}

