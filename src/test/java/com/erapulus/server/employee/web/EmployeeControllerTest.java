package com.erapulus.server.employee.web;

import com.erapulus.server.TestUtils;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.dto.EmployeeRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import com.erapulus.server.employee.service.EmployeeService;
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
import org.springframework.security.access.AccessDeniedException;
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

@WebFluxTest(controllers = {EmployeeRouter.class, EmployeeController.class},
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class EmployeeControllerTest {

    private final static int EMPLOYEE_ID_1 = 1;
    private final static int EMPLOYEE_ID_2 = 2;
    private final static Integer UNIVERSITY_ID = 3;

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
    void listEmployees_shouldReturnEmployees() {
        // given
        var employeeList = List.of(createEmployeeResponseDto(EMPLOYEE_ID_1), createEmployeeResponseDto(EMPLOYEE_ID_2));
        String expectedPayload = """
                [
                  {
                     "id":1,
                     "type":"EMPLOYEE",
                     "firstName":"firstName",
                     "lastName":"lastName",
                     "universityId":3,
                     "email":"example@gmail.com",
                     "phoneNumber":"+48 123 456 789"
                  },
                  {
                     "id":2,
                     "type":"EMPLOYEE",
                     "firstName":"firstName",
                     "lastName":"lastName",
                     "universityId":3,
                     "email":"example@gmail.com",
                     "phoneNumber":"+48 123 456 789"
                  }
                ]""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(employeeService.listEmployees(UNIVERSITY_ID)).thenReturn(Mono.just(employeeList));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/employee")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void listEmployees_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(employeeService.listEmployees(UNIVERSITY_ID)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/university/{universityId}/employee")
                             .build(UNIVERSITY_ID))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() {
        // given
        var employeeResponseDto = createEmployeeResponseDto(EMPLOYEE_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "type":"EMPLOYEE",
                   "firstName":"firstName",
                   "lastName":"lastName",
                   "universityId":3,
                   "email":"example@gmail.com",
                   "phoneNumber":"+48 123 456 789"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(employeeService.getEmployeeById(EMPLOYEE_ID_1)).thenReturn(Mono.just(employeeResponseDto));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getEmployeeById_shouldReturnForbiddenWhenAccessDeniedExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.FORBIDDEN.value(), "forbidden");
        when(employeeService.getEmployeeById(EMPLOYEE_ID_1)).thenReturn(Mono.error(new AccessDeniedException("access.denied")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getEmployeeById_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "employee.not.found");
        when(employeeService.getEmployeeById(EMPLOYEE_ID_1)).thenReturn(Mono.error(new NoSuchElementException("employee")));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void getEmployeeById_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(employeeService.getEmployeeById(EMPLOYEE_ID_1)).thenReturn(Mono.error(new RuntimeException()));

        // when-then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnUpdatedEmployeeWhenDataCorrect() {
        // given
        var employeeRequestDto = createEmployeeRequestDto();
        var employeeResponseDto = createEmployeeResponseDto(EMPLOYEE_ID_1);
        String expectedPayload = """
                {
                   "id":1,
                   "type":"EMPLOYEE",
                   "firstName":"firstName",
                   "lastName":"lastName",
                   "universityId":3,
                   "email":"example@gmail.com",
                   "phoneNumber":"+48 123 456 789"
                }""";
        String expectedResponse = TestUtils.createSuccessfulResponse(HttpStatus.OK.value(), expectedPayload);
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenReturn(Mono.just(employeeResponseDto));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.OK)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnBadRequestWhenMissingField() {
        // given
        var employeeRequestDto = createEmployeeRequestDto().firstName(null);
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;firstName.must.not.be.null");
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenThrow(new ConstraintViolationException(validator.validate(employeeRequestDto)));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnBadRequestWhenNoBodyFound() {
        // given
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.BAD_REQUEST.value(), "bad.request;not.found.body");
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenThrow(new IllegalStateException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnForbiddenWhenAccessDeniedExceptionThrown() {
        // given
        var employeeRequestDto = createEmployeeRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.FORBIDDEN.value(), "forbidden");
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenReturn(Mono.error(new AccessDeniedException("access.denied")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnNotFoundWhenNoSuchElementExceptionThrown() {
        // given
        var employeeRequestDto = createEmployeeRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.NOT_FOUND.value(), "employee.not.found");
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenReturn(Mono.error(new NoSuchElementException("employee")));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnConflictWhenEmployeeDuplicated() {
        // given
        var employeeRequestDto = createEmployeeRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.CONFLICT.value(), "employee.conflict");
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenThrow(new DuplicateKeyException("employee"));

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    @Test
    void updateEmployee_shouldReturnInternalServerErrorWhenUnexpectedErrorThrown() {
        // given
        var employeeRequestDto = createEmployeeRequestDto();
        String expectedResponse = TestUtils.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "internal.server.error");
        when(employeeService.updateEmployee(any(EmployeeRequestDto.class), eq(EMPLOYEE_ID_1)))
                .thenThrow(new RuntimeException());

        // when-then
        webTestClient.put()
                     .uri(uriBuilder -> uriBuilder
                             .path("/api/employee/{employeeId}")
                             .build(EMPLOYEE_ID_1))
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(BodyInserters.fromValue(TestUtils.parseToJson(employeeRequestDto)))
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                     .expectBody().consumeWith(body -> TestUtils.assertJsonEquals(expectedResponse, TestUtils.getBodyAsString(body)));
    }

    private EmployeeResponseDto createEmployeeResponseDto(int id) {
        return EmployeeResponseDto.builder()
                                  .id(id)
                                  .type(UserType.EMPLOYEE)
                                  .firstName("firstName")
                                  .lastName("lastName")
                                  .email("example@gmail.com")
                                  .universityId(UNIVERSITY_ID)
                                  .phoneNumber("+48 123 456 789")
                                  .build();
    }

    private EmployeeRequestDto createEmployeeRequestDto() {
        return EmployeeRequestDto.builder()
                                 .firstName("firstName")
                                 .lastName("lastName")
                                 .email("example@gmail.com")
                                 .phoneNumber("+48 123 456 789")
                                 .build();
    }
}

