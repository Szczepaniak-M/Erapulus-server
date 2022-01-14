package com.erapulus.server.applicationuser.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.applicationuser.service.RegisterService;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.dto.EmployeeCreateRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {RegisterRouter.class, RegisterController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class RegisterControllerTest {

    private static final int USER_ID = 1;
    private static final int UNIVERSITY_ID = 2;
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "example@gmail.com";
    private static final String PASSWORD = "password";
    private static final String PHONE_NUMBER = "+48123456789";

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    RegisterService registerService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void createAdministrator_shouldReturnCreatedWhenUserCorrect() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(null);
        var employeeResponseDto = createAdministratorResponseDto();
        String expectedPayload = """
                {
                  "id":1,
                  "type":"ADMINISTRATOR",
                  "firstName":"firstName",
                  "lastName":"lastName",
                  "universityId":null,
                  "email":"example@gmail.com",
                  "phoneNumber":"+48123456789"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(registerService.createAdministrator(any(EmployeeCreateRequestDto.class))).thenReturn(Mono.just(employeeResponseDto));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createAdministrator_shouldReturnBadRequestWhenMissingField() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(null).email(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;email.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(registerService.createAdministrator(any(EmployeeCreateRequestDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(employeeCreateRequestDto)));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createAdministrator_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(registerService.createAdministrator(any(EmployeeCreateRequestDto.class))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createAdministrator_shouldReturnConflictWhenAdministratorDuplicated() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "administrator.conflict");
        when(registerService.createAdministrator(any(EmployeeCreateRequestDto.class))).thenThrow(new DuplicateKeyException("employee"));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createAdministrator_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(registerService.createAdministrator(any(EmployeeCreateRequestDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityAdministrator_shouldReturnCreatedWhenUserCorrect() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID);
        var employeeResponseDto = createUniversityAdministratorResponseDto();
        String expectedPayload = """
                {
                  "id":1,
                  "type":"UNIVERSITY_ADMINISTRATOR",
                  "firstName":"firstName",
                  "lastName":"lastName",
                  "universityId":2,
                  "email":"example@gmail.com",
                  "phoneNumber":"+48123456789"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(registerService.createUniversityAdministrator(any(EmployeeCreateRequestDto.class))).thenReturn(Mono.just(employeeResponseDto));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/university-administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityAdministrator_shouldReturnBadRequestWhenMissingField() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID).email(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;email.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(registerService.createUniversityAdministrator(any(EmployeeCreateRequestDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(employeeCreateRequestDto)));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/university-administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityAdministrator_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(registerService.createUniversityAdministrator(any(EmployeeCreateRequestDto.class))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/university-administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityAdministrator_shouldReturnConflictWhenEmployeeDuplicated() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "universityAdministrator.conflict");
        when(registerService.createUniversityAdministrator(any(EmployeeCreateRequestDto.class))).thenThrow(new DuplicateKeyException("universityAdministrator"));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/university-administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityAdministrator_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(registerService.createUniversityAdministrator(any(EmployeeCreateRequestDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/university-administrator")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityEmployee_shouldReturnCreatedWhenUserCorrect() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID);
        var employeeResponseDto = createUniversityEmployeeResponseDto();
        String expectedPayload = """
                {
                  "id":1,
                  "type":"EMPLOYEE",
                  "firstName":"firstName",
                  "lastName":"lastName",
                  "universityId":2,
                  "email":"example@gmail.com",
                  "phoneNumber":"+48123456789"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.CREATED.value(), expectedPayload);
        when(registerService.createUniversityEmployee(any(EmployeeCreateRequestDto.class))).thenReturn(Mono.just(employeeResponseDto));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityEmployee_shouldReturnBadRequestWhenMissingField() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID).email(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;email.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(registerService.createUniversityEmployee(any(EmployeeCreateRequestDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(employeeCreateRequestDto)));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityEmployee_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(registerService.createUniversityEmployee(any(EmployeeCreateRequestDto.class))).thenThrow(new DuplicateKeyException("employee"));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityEmployee_shouldReturnConflictWhenEmployeeDuplicated() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "employee.conflict");
        when(registerService.createUniversityEmployee(any(EmployeeCreateRequestDto.class))).thenThrow(new DuplicateKeyException("employee"));

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createUniversityEmployee_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var employeeCreateRequestDto = createEmployeeCreateRequestDto(UNIVERSITY_ID);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(registerService.createUniversityEmployee(any(EmployeeCreateRequestDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private EmployeeCreateRequestDto createEmployeeCreateRequestDto(Integer universityId) {
        return EmployeeCreateRequestDto.builder()
                                       .firstName(FIRST_NAME)
                                       .lastName(LAST_NAME)
                                       .universityId(universityId)
                                       .email(EMAIL)
                                       .password(PASSWORD)
                                       .build();
    }

    private EmployeeResponseDto createAdministratorResponseDto() {
        return createEmployeeResponseDto(UserType.ADMINISTRATOR, null);
    }

    private EmployeeResponseDto createUniversityAdministratorResponseDto() {
        return createEmployeeResponseDto(UserType.UNIVERSITY_ADMINISTRATOR, UNIVERSITY_ID);
    }

    private EmployeeResponseDto createUniversityEmployeeResponseDto() {
        return createEmployeeResponseDto(UserType.EMPLOYEE, UNIVERSITY_ID);
    }

    private EmployeeResponseDto createEmployeeResponseDto(UserType userType, Integer universityId) {
        return EmployeeResponseDto.builder()
                                  .id(USER_ID)
                                  .type(userType)
                                  .firstName(FIRST_NAME)
                                  .lastName(LAST_NAME)
                                  .universityId(universityId)
                                  .email(EMAIL)
                                  .phoneNumber(PHONE_NUMBER)
                                  .build();
    }
}

