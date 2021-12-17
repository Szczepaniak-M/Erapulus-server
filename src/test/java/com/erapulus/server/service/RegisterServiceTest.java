package com.erapulus.server.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.repository.EmployeeRepository;
import com.erapulus.server.dto.EmployeeCreateRequestDto;
import com.erapulus.server.dto.EmployeeCreatedDto;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        registerService = new RegisterService(bCryptPasswordEncoder, employeeRepository);
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
        Mono<EmployeeCreatedDto> result = registerService.createUniversityEmployee(employeeCreateRequestDto);

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
        when(employeeRepository.save(any(EmployeeEntity.class))).thenThrow(new DataIntegrityViolationException("Duplicated"));

        // when
        Mono<EmployeeCreatedDto> result = registerService.createUniversityEmployee(employeeCreateRequestDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .verifyError(DataIntegrityViolationException.class);
    }
}
