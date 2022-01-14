package com.erapulus.server.applicationuser.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.applicationuser.dto.EmployeeLoginDto;
import com.erapulus.server.applicationuser.dto.LoginResponseDto;
import com.erapulus.server.applicationuser.dto.StudentLoginDto;
import com.erapulus.server.applicationuser.service.LoginService;
import com.erapulus.server.common.exception.InvalidTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {LoginRouter.class, LoginController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class LoginControllerTest {

    public static final String EMAIL = "example@gmail.com";
    public static final String PASSWORD = "password";
    public static final String ACCESS_TOKEN = "accessToken.accessToken.accessToken";
    public static final String JWT_TOKEN = "token.token.token";
    private static final int USER_ID = 1;
    private static final int UNIVERSITY_ID = 2;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    LoginService loginService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void loginEmployee_shouldReturnTokenWhenCredentialsCorrect() {
        // given
        var employeeLoginDto = createEmployeeLoginDto();
        var loginResponseDto = createLoginResponseDto();
        String expectedPayload = """
                {
                  "userId":1,
                  "token":"token.token.token",
                  "universityId":2
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(loginService.validateEmployeeCredentials(any(EmployeeLoginDto.class))).thenReturn(Mono.just(loginResponseDto));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeLoginDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void loginEmployee_shouldReturnBadRequestWhenMissingField() {
        // given
        var employeeCreateRequestDto = createEmployeeLoginDto().email(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;email.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(loginService.validateEmployeeCredentials(any(EmployeeLoginDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(employeeCreateRequestDto)));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void loginEmployee_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(loginService.validateEmployeeCredentials(any(EmployeeLoginDto.class))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void loginEmployee_shouldReturnBadRequestWhenWrongPassword() {
        // given
        var employeeCreateRequestDto = createEmployeeLoginDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;invalid.credentials");
        when(loginService.validateEmployeeCredentials(any(EmployeeLoginDto.class))).thenThrow(new BadCredentialsException("login"));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void loginEmployee_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var employeeLoginDto = createEmployeeLoginDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(loginService.validateEmployeeCredentials(any(EmployeeLoginDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeLoginDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateGoogleStudentCredentials_shouldReturnTokenWhenCredentialsCorrect() {
        // given
        var studentLoginDTO = createStudentLoginDto();
        var loginResponseDto = createLoginResponseDto();
        String expectedPayload = """
                 {
                  "userId":1,
                  "token":"token.token.token",
                  "universityId":2
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(loginService.validateGoogleStudentCredentials(any(StudentLoginDto.class))).thenReturn(Mono.just(loginResponseDto));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/google")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateGoogleStudentCredentials_shouldReturnBadRequestWhenMissingField() {
        // given
        var studentLoginDTO = createStudentLoginDto().token(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;token.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(loginService.validateGoogleStudentCredentials(any(StudentLoginDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(studentLoginDTO)));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/google")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateGoogleStudentCredentials_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(loginService.validateGoogleStudentCredentials(any(StudentLoginDto.class))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/google")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateGoogleStudentCredentials_shouldReturnBadRequestWhenWrongToken() {
        // given
        var studentLoginDTO = createStudentLoginDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;invalid.credentials");
        when(loginService.validateGoogleStudentCredentials(any(StudentLoginDto.class))).thenThrow(new InvalidTokenException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/google")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateGoogleStudentCredentials_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var studentLoginDTO = createStudentLoginDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(loginService.validateGoogleStudentCredentials(any(StudentLoginDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/google")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateFacebookStudentCredentials_shouldReturnTokenWhenCredentialsCorrect() {
        // given
        var studentLoginDTO = createStudentLoginDto();
        var loginResponseDto = createLoginResponseDto();
        String expectedPayload = """
                 {
                  "userId":1,
                  "token":"token.token.token",
                  "universityId":2
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(loginService.validateFacebookStudentCredentials(any(StudentLoginDto.class))).thenReturn(Mono.just(loginResponseDto));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/facebook")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateFacebookStudentCredentials_shouldReturnBadRequestWhenMissingField() {
        // given
        var studentLoginDTO = createStudentLoginDto().token(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;token.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(loginService.validateFacebookStudentCredentials(any(StudentLoginDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(studentLoginDTO)));

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/facebook")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateFacebookStudentCredentials_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(loginService.validateFacebookStudentCredentials(any(StudentLoginDto.class))).thenThrow(new IllegalStateException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/facebook")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateFacebookStudentCredentials_shouldReturnBadRequestWhenWrongToken() {
        // given
        var studentLoginDTO = createStudentLoginDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;invalid.credentials");
        when(loginService.validateFacebookStudentCredentials(any(StudentLoginDto.class))).thenThrow(new InvalidTokenException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/facebook")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void validateFacebookStudentCredentials_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var studentLoginDTO = createStudentLoginDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(loginService.validateFacebookStudentCredentials(any(StudentLoginDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/api/user/login/facebook")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(studentLoginDTO)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private LoginResponseDto createLoginResponseDto() {
        return LoginResponseDto.builder()
                               .token(JWT_TOKEN)
                               .userId(USER_ID)
                               .universityId(UNIVERSITY_ID)
                               .build();
    }

    private EmployeeLoginDto createEmployeeLoginDto() {
        return EmployeeLoginDto.builder()
                               .email(EMAIL)
                               .password(PASSWORD)
                               .build();
    }

    private StudentLoginDto createStudentLoginDto() {
        return StudentLoginDto.builder()
                              .token(ACCESS_TOKEN)
                              .build();
    }
}

