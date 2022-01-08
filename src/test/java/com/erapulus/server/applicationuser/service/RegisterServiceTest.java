package com.erapulus.server.applicationuser.service;

import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.database.EmployeeRepository;
import com.erapulus.server.employee.dto.EmployeeCreateRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import com.erapulus.server.employee.mapper.EmployeeEntityToResponseDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

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

    RegisterService registerService;

    @BeforeEach
    void setUp() {
        registerService = new RegisterService(bCryptPasswordEncoder, employeeRepository, new EmployeeEntityToResponseDtoMapper());
    }

    @Test
    void createAdministrator_shouldReturnEmployeeCreatedDtoWhenSuccess() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = EmployeeCreateRequestDto.builder()
                                                                                    .email(EMAIL)
                                                                                    .password(PASSWORD)
                                                                                    .firstName(FIRST_NAME)
                                                                                    .lastName(LAST_NAME)
                                                                                    .build();
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(employeeRepository.save(any(EmployeeEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, EmployeeEntity.class).id(ID)));

        // when
        Mono<EmployeeResponseDto> result = registerService.createAdministrator(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeCreatedDto -> {
                        assertEquals(ID, employeeCreatedDto.id());
                        assertEquals(EMAIL, employeeCreatedDto.email());
                        assertEquals(FIRST_NAME, employeeCreatedDto.firstName());
                        assertEquals(LAST_NAME, employeeCreatedDto.lastName());
                        assertNull(employeeCreatedDto.universityId());
                        assertEquals(UserType.ADMINISTRATOR, employeeCreatedDto.type());
                    })
                    .verifyComplete();
    }

    @Test
    void createAdministrator_shouldDataIntegrityViolationWhenEmailDuplicated() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = EmployeeCreateRequestDto.builder()
                                                                                    .email(EMAIL)
                                                                                    .password(PASSWORD)
                                                                                    .firstName(FIRST_NAME)
                                                                                    .lastName(LAST_NAME)
                                                                                    .universityId(UNIVERSITY)
                                                                                    .build();
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicated"));

        // when
        Mono<EmployeeResponseDto> result = registerService.createAdministrator(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyError(DataIntegrityViolationException.class);
    }


    @Test
    void createUniversityAdministrator_shouldReturnEmployeeCreatedDtoWhenSuccess() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = EmployeeCreateRequestDto.builder()
                                                                                    .email(EMAIL)
                                                                                    .password(PASSWORD)
                                                                                    .firstName(FIRST_NAME)
                                                                                    .lastName(LAST_NAME)
                                                                                    .universityId(UNIVERSITY)
                                                                                    .build();
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(employeeRepository.save(any(EmployeeEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, EmployeeEntity.class).id(ID)));

        // when
        Mono<EmployeeResponseDto> result = registerService.createUniversityAdministrator(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeCreatedDto -> {
                        assertEquals(ID, employeeCreatedDto.id());
                        assertEquals(EMAIL, employeeCreatedDto.email());
                        assertEquals(FIRST_NAME, employeeCreatedDto.firstName());
                        assertEquals(LAST_NAME, employeeCreatedDto.lastName());
                        assertEquals(UNIVERSITY, employeeCreatedDto.universityId());
                        assertEquals(UserType.UNIVERSITY_ADMINISTRATOR, employeeCreatedDto.type());
                    })
                    .verifyComplete();
    }

    @Test
    void createUniversityAdministrator_shouldDataIntegrityViolationWhenEmailDuplicated() {
        // given
        EmployeeCreateRequestDto employeeCreateRequestDto = EmployeeCreateRequestDto.builder()
                                                                                    .email(EMAIL)
                                                                                    .password(PASSWORD)
                                                                                    .firstName(FIRST_NAME)
                                                                                    .lastName(LAST_NAME)
                                                                                    .universityId(UNIVERSITY)
                                                                                    .build();
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicated"));

        // when
        Mono<EmployeeResponseDto> result = registerService.createUniversityEmployee(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyError(DataIntegrityViolationException.class);
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
        when(employeeRepository.save(any(EmployeeEntity.class)))
                .then(invocationOnMock -> Mono.just(invocationOnMock.getArgument(0, EmployeeEntity.class).id(ID)));

        // when
        Mono<EmployeeResponseDto> result = registerService.createUniversityEmployee(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(employeeCreatedDto -> {
                        assertEquals(ID, employeeCreatedDto.id());
                        assertEquals(EMAIL, employeeCreatedDto.email());
                        assertEquals(FIRST_NAME, employeeCreatedDto.firstName());
                        assertEquals(LAST_NAME, employeeCreatedDto.lastName());
                        assertEquals(UNIVERSITY, employeeCreatedDto.universityId());
                        assertEquals(UserType.EMPLOYEE, employeeCreatedDto.type());
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
        when(employeeRepository.save(any(EmployeeEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicated"));

        // when
        Mono<EmployeeResponseDto> result = registerService.createUniversityEmployee(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyError(DataIntegrityViolationException.class);
    }
}
