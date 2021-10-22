package pl.put.erasmusbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.put.erasmusbackend.database.model.Employee;
import pl.put.erasmusbackend.database.repository.EmployeeRepository;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final int ID = 1;
    private static final CharSequence PASSWORD = "password";
    private static final String ENCRYPTED_PASSWORD = "encryptedPassword";
    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final int UNIVERSITY = 2;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    EmployeeRepository employeeRepository;

    EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(bCryptPasswordEncoder, employeeRepository);
    }

    @Test
    void createEmployee_shouldReturnEmployeeCreatedDtoWhenSuccess() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = EmployeeCreateRequestDto.builder()
                                                                                    .email(EMAIL)
                                                                                    .password(PASSWORD)
                                                                                    .firstName(FIRST_NAME)
                                                                                    .lastName(LAST_NAME)
                                                                                    .universityId(UNIVERSITY)
                                                                                    .build();
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(employeeRepository.save(any(Employee.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, Employee.class).id(ID)));

        // when
        Mono<EmployeeCreatedDto> result = employeeService.createEmployee(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeCreatedDto -> {
                        assertEquals(ID, employeeCreatedDto.id());
                        assertEquals(EMAIL, employeeCreatedDto.email());
                        assertEquals(FIRST_NAME, employeeCreatedDto.firstName());
                        assertEquals(LAST_NAME, employeeCreatedDto.lastName());
                        assertEquals(UNIVERSITY, employeeCreatedDto.universityId());
                    })
                .verifyComplete();
    }

    @Test
    void createEmployee_shouldDataIntegrityViolationWhenEmailDuplicated() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = EmployeeCreateRequestDto.builder()
                                                                                    .email(EMAIL)
                                                                                    .password(PASSWORD)
                                                                                    .firstName(FIRST_NAME)
                                                                                    .lastName(LAST_NAME)
                                                                                    .universityId(UNIVERSITY)
                                                                                    .build();
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(employeeRepository.save(any(Employee.class))).thenThrow(new DataIntegrityViolationException("Duplicated"));

        // when
        Mono<EmployeeCreatedDto> result = employeeService.createEmployee(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyError(DataIntegrityViolationException.class);
    }
}
