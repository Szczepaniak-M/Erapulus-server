package com.erapulus.server.employee.service;

import com.erapulus.server.applicationuser.database.ApplicationUserEntity;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.database.EmployeeRepository;
import com.erapulus.server.employee.dto.EmployeeRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import com.erapulus.server.employee.mapper.EmployeeEntityToResponseDtoMapper;
import com.erapulus.server.employee.mapper.EmployeeRequestDtoToEntityMapper;
import com.erapulus.server.security.JwtAuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    public static final int UNIVERSITY_ID_1 = 1;
    public static final int UNIVERSITY_ID_2 = 2;
    public static final String PASSWORD = "password";
    private final static int ID_1 = 1;
    private final static int ID_2 = 2;
    @Mock
    EmployeeRepository employeeRepository;

    EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository,
                new EmployeeRequestDtoToEntityMapper(),
                new EmployeeEntityToResponseDtoMapper());
    }

    @Test
    void listEmployees_shouldReturnEmployeeList() {
        // given
        var employee1 = createEmployee(ID_1);
        var employee2 = createEmployee(ID_2);
        when(employeeRepository.findAllByUniversityIdAndType(UNIVERSITY_ID_1)).thenReturn(Flux.just(employee1, employee2));

        // when
        Mono<List<EmployeeResponseDto>> result = employeeService.listEmployees(UNIVERSITY_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employees -> assertEquals(2, employees.size()))
                    .verifyComplete();
    }

    @Test
    void getEmployeeById_shouldReturnEmployeeWhenFound() {
        // given
        var employee = createEmployee(ID_1);
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(employee));
        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext(UserType.EMPLOYEE, UNIVERSITY_ID_1));


            // when
            Mono<EmployeeResponseDto> result = employeeService.getEmployeeById(ID_1);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .assertNext(employeeResponseDto -> {
                            assertEquals(ID_1, employeeResponseDto.id());
                            assertEquals(UNIVERSITY_ID_1, employeeResponseDto.universityId());
                        })
                        .verifyComplete();
        }
    }

    @Test
    void getEmployeeById_shouldThrowAccessDeniedExceptionWhenWrongRole() {
        // given
        var employee = createEmployee(ID_1);
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(employee));
        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext(UserType.ADMINISTRATOR, null));


            // when
            Mono<EmployeeResponseDto> result = employeeService.getEmployeeById(ID_1);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .expectError(AccessDeniedException.class)
                        .verify();
        }
    }

    @Test
    void getEmployeeById_shouldThrowAccessDeniedExceptionWhenWrongUniversity() {
        // given
        var employee = createEmployee(ID_1);
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(employee));
        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext(UserType.EMPLOYEE, UNIVERSITY_ID_2));


            // when
            Mono<EmployeeResponseDto> result = employeeService.getEmployeeById(ID_1);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .expectError(AccessDeniedException.class)
                        .verify();
        }
    }

    @Test
    void getEmployeeById_shouldThrowExceptionWhenEmployeeNotFound() {
        // given
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<EmployeeResponseDto> result = employeeService.getEmployeeById(ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void updateEmployee_shouldUpdateEmployeeWhenFound() {
        // given
        var employee = createEmployee(ID_1);
        var employeeRequestDto = new EmployeeRequestDto();
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(employee));
        when(employeeRepository.save(any(EmployeeEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, EmployeeEntity.class).id(ID_1)));

        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext(UserType.EMPLOYEE, UNIVERSITY_ID_1));

            // when
            Mono<EmployeeResponseDto> result = employeeService.updateEmployee(employeeRequestDto, ID_1);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .assertNext(employeeResponseDto -> {
                            assertEquals(ID_1, employeeResponseDto.id());
                            assertEquals(UNIVERSITY_ID_1, employeeResponseDto.universityId());
                            assertEquals(UserType.EMPLOYEE, employeeResponseDto.type());
                        })
                        .verifyComplete();
        }
    }

    @Test
    void updateEmployee_shouldThrowAccessDeniedExceptionWhenWrongRole() {
        // given
        var employee = createEmployee(ID_1);
        var employeeRequestDto = new EmployeeRequestDto();
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(employee));
        when(employeeRepository.save(any(EmployeeEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, EmployeeEntity.class).id(ID_1)));

        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext(UserType.ADMINISTRATOR, UNIVERSITY_ID_1));

            // when
            Mono<EmployeeResponseDto> result = employeeService.updateEmployee(employeeRequestDto, ID_1);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .expectError(AccessDeniedException.class)
                        .verify();
        }
    }

    @Test
    void updateEmployee_shouldThrowAccessDeniedExceptionWhenWrongUniversity() {
        // given
        var employee = createEmployee(ID_1);
        var employeeRequestDto = new EmployeeRequestDto();
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.just(employee));
        when(employeeRepository.save(any(EmployeeEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, EmployeeEntity.class).id(ID_1)));

        try (MockedStatic<ReactiveSecurityContextHolder> utilities = mockStatic(ReactiveSecurityContextHolder.class)) {
            utilities.when(ReactiveSecurityContextHolder::getContext).thenReturn(createSecurityContext(UserType.EMPLOYEE, UNIVERSITY_ID_2));

            // when
            Mono<EmployeeResponseDto> result = employeeService.updateEmployee(employeeRequestDto, ID_1);

            // then
            StepVerifier.create(result)
                        .expectSubscription()
                        .expectError(AccessDeniedException.class)
                        .verify();
        }
    }

    @Test
    void updateEmployee_shouldThrowExceptionWhenEmployeeNotFound() {
        // given
        var employeeRequestDto = new EmployeeRequestDto();
        when(employeeRepository.findByIdAndType(ID_1)).thenReturn(Mono.empty());

        // when
        Mono<EmployeeResponseDto> result = employeeService.updateEmployee(employeeRequestDto, ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchElementException.class)
                    .verify();
    }

    @Test
    void deleteAllEmployeesByUniversityId() {
        // when
        when(employeeRepository.deleteAllByUniversityId(UNIVERSITY_ID_1)).thenReturn(Mono.empty());

        // given
        Mono<Void> result = employeeService.deleteAllEmployeesByUniversityId(UNIVERSITY_ID_1);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyComplete();
    }

    private EmployeeEntity createEmployee(int id) {
        return EmployeeEntity.builder()
                             .id(id)
                             .type(UserType.EMPLOYEE)
                             .firstName("firstName")
                             .lastName("lastName")
                             .universityId(UNIVERSITY_ID_1)
                             .password(PASSWORD)
                             .build();
    }

    private Mono<SecurityContext> createSecurityContext(UserType userType, Integer universityId) {
        return Mono.just(new SecurityContextImpl(
                new JwtAuthenticatedUser(
                        ApplicationUserEntity.builder()
                                             .id(ID_1)
                                             .type(userType)
                                             .firstName("firstName")
                                             .lastName("lastName")
                                             .universityId(universityId)
                                             .build(),
                        Collections.emptyList())));
    }
}