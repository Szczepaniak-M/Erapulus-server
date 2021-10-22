package pl.put.erasmusbackend.web.controller;

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
import pl.put.erasmusbackend.TestUtils;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;
import pl.put.erasmusbackend.service.EmployeeService;
import pl.put.erasmusbackend.web.router.EmployeeRouter;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = {EmployeeRouter.class, EmployeeController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class EmployeeControllerTest {

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "example@gmail.com";
    public static final String PASSWORD = "password";
    public static final int UNIVERSITY_ID = 2;
    public static final int ID = 1;

    @Autowired
    ApplicationContext applicationContext;

    @MockBean
    EmployeeService employeeService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext).build();
    }

    @Test
    void createEmployee_shouldReturnCreatedWhenUserCorrect() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = new EmployeeCreateRequestDto().firstName(FIRST_NAME)
                                                                                          .lastName(LAST_NAME)
                                                                                          .universityId(UNIVERSITY_ID)
                                                                                          .email(EMAIL)
                                                                                          .password(PASSWORD);

        EmployeeCreatedDto employeeCreatedDto = new EmployeeCreatedDto().id(ID)
                                                                        .firstName(FIRST_NAME)
                                                                        .lastName(LAST_NAME)
                                                                        .universityId(UNIVERSITY_ID)
                                                                        .email(EMAIL);
        String expected = "{\"status\":201," +
                "\"payload\":{" +
                "\"id\":1," +
                "\"firstName\":\"firstName\"," +
                "\"lastName\":\"lastName\"," +
                "\"university\":2," +
                "\"email\":\"example@gmail.com\"" +
                "}," +
                "\"message\":null}";
        when(employeeService.createEmployee(any(EmployeeCreateRequestDto.class))).thenReturn(Mono.just(employeeCreatedDto));

        // when-then
        webTestClient.post()
                     .uri("/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CREATED)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expected, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createEmployee_shouldReturnBadRequestWhenMissingField() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = new EmployeeCreateRequestDto().firstName(FIRST_NAME)
                                                                                          .lastName(LAST_NAME)
                                                                                          .universityId(UNIVERSITY_ID)
                                                                                          .password(PASSWORD);
        String expected = "{\"status\":400," +
                "\"payload\":null," +
                "\"message\":\"bad.request;email.must.not.be.null\"}";
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(employeeService.createEmployee(any(EmployeeCreateRequestDto.class)))
                .thenThrow(new ConstraintViolationException(validator.validate(employeeCreateRequestDto)));

        // when-then
        webTestClient.post()
                     .uri("/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expected, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createEmployee_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expected = "{\"status\":400," +
                "\"payload\":null," +
                "\"message\":\"bad.request;not.found.body\"}";
        when(employeeService.createEmployee(any(EmployeeCreateRequestDto.class))).thenThrow(new DuplicateKeyException("employee"));

        // when-then
        webTestClient.post()
                     .uri("/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expected, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createEmployee_shouldReturnConflictWhenEmployeeDuplicated() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = new EmployeeCreateRequestDto().firstName(FIRST_NAME)
                                                                                          .lastName(LAST_NAME)
                                                                                          .universityId(UNIVERSITY_ID)
                                                                                          .email(EMAIL)
                                                                                          .password(PASSWORD);

        String expected = "{\"status\":409," +
                "\"payload\":null," +
                "\"message\":\"employee.conflict\"}";
        when(employeeService.createEmployee(any(EmployeeCreateRequestDto.class))).thenThrow(new DuplicateKeyException("employee"));

        // when-then
        webTestClient.post()
                     .uri("/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expected, TestUtils.getBodyAsString(body)));
    }

    @Test
    void createEmployee_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = new EmployeeCreateRequestDto().firstName(FIRST_NAME)
                                                                                          .lastName(LAST_NAME)
                                                                                          .universityId(UNIVERSITY_ID)
                                                                                          .email(EMAIL)
                                                                                          .password(PASSWORD);

        String expected = "{\"status\":500," +
                "\"payload\":null," +
                "\"message\":\"internal.server.error\"}";
        when(employeeService.createEmployee(any(EmployeeCreateRequestDto.class))).thenThrow(new RuntimeException());

        // when-then
        webTestClient.post()
                     .uri("/register/employee")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeCreateRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expected, TestUtils.getBodyAsString(body)));
    }
}
