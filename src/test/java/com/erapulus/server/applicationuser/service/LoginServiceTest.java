package com.erapulus.server.applicationuser.service;

import com.erapulus.server.applicationuser.dto.LoginResponseDTO;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.common.exception.InvalidPasswordException;
import com.erapulus.server.common.exception.InvalidTokenException;
import com.erapulus.server.common.exception.NoSuchUserException;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.database.EmployeeRepository;
import com.erapulus.server.employee.dto.EmployeeLoginDTO;
import com.erapulus.server.security.FacebookTokenValidator;
import com.erapulus.server.security.GoogleTokenValidator;
import com.erapulus.server.security.JwtGenerator;
import com.erapulus.server.student.database.StudentEntity;
import com.erapulus.server.student.database.StudentRepository;
import com.erapulus.server.student.dto.StudentLoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    private static final String PASSWORD = "password";
    private static final String EMAIL = "example@gmail.com";
    private static final String TOKEN = "token.token.token";
    private static final String ACCESS_TOKEN = "accessToken.accessToken.accessToken";
    private static final int ID = 1;
    private static final int UNIVERSITY_ID = 2;
    private static final String PICTURE_URL = "example.com";


    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    StudentRepository studentRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    JwtGenerator jwtGenerator;

    @Mock
    GoogleTokenValidator googleTokenValidator;

    @Mock
    FacebookTokenValidator facebookTokenValidator;

    LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(employeeRepository, studentRepository, bCryptPasswordEncoder, jwtGenerator, googleTokenValidator, facebookTokenValidator);
    }

    @Test
    void validateEmployeeCredentials_shouldReturnTokenOnSuccessAuthentication() {
        // when
        var employeeLoginDto = new EmployeeLoginDTO(EMAIL, PASSWORD);
        var employee = createEmployee();
        when(employeeRepository.findByEmailAndType(EMAIL)).thenReturn(Mono.just(employee));
        when(bCryptPasswordEncoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
        when(jwtGenerator.generate(employee)).thenReturn(TOKEN);

        // given
        Mono<LoginResponseDTO> result = loginService.validateEmployeeCredentials(employeeLoginDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> {
                        assertEquals(ID, response.userId());
                        assertEquals(UNIVERSITY_ID, response.universityId());
                        assertEquals(TOKEN, response.token());
                    })
                    .verifyComplete();
    }

    @Test
    void validateEmployeeCredentials_shouldThrowExceptionWhenEmployeeNotFound() {
        // when
        var employeeLoginDto = new EmployeeLoginDTO(EMAIL, PASSWORD);
        when(employeeRepository.findByEmailAndType(EMAIL)).thenReturn(Mono.empty());

        // given
        Mono<LoginResponseDTO> result = loginService.validateEmployeeCredentials(employeeLoginDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(NoSuchUserException.class)
                    .verify();
    }

    @Test
    void validateEmployeeCredentials_shouldThrowExceptionWhenWrongPassword() {
        // when
        String wrongPassword = PASSWORD + "a";
        var employeeLoginDto = new EmployeeLoginDTO(EMAIL, wrongPassword);
        var employee = createEmployee();
        when(employeeRepository.findByEmailAndType(EMAIL)).thenReturn(Mono.just(employee));
        when(bCryptPasswordEncoder.matches(wrongPassword, PASSWORD)).thenReturn(false);

        // given
        Mono<LoginResponseDTO> result = loginService.validateEmployeeCredentials(employeeLoginDto);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(InvalidPasswordException.class)
                    .verify();
    }

    @Test
    void validateGoogleStudentCredentials_shouldReturnTokenOnSuccessAuthentication() {
        // when
        var studentLoginDTO = new StudentLoginDTO(ACCESS_TOKEN);
        var studentEntity = createStudent();
        when(googleTokenValidator.validate(studentLoginDTO)).thenReturn(Mono.just(studentEntity));
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Mono.just(studentEntity));
        when(studentRepository.save(studentEntity)).thenReturn(Mono.error(IllegalStateException::new));
        when(jwtGenerator.generate(studentEntity)).thenReturn(TOKEN);

        // given
        Mono<LoginResponseDTO> result = loginService.validateGoogleStudentCredentials(studentLoginDTO);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> {
                        assertEquals(ID, response.userId());
                        assertEquals(UNIVERSITY_ID, response.universityId());
                        assertEquals(TOKEN, response.token());
                    })
                    .verifyComplete();
    }

    @Test
    void validateGoogleStudentCredentials_shouldSaveUserIfLoginFirstTime() {
        var studentLoginDTO = new StudentLoginDTO(ACCESS_TOKEN);
        var studentEntity = createStudent();
        when(googleTokenValidator.validate(studentLoginDTO)).thenReturn(Mono.just(studentEntity));
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Mono.empty());
        when(studentRepository.save(studentEntity)).thenReturn(Mono.just(studentEntity));
        when(jwtGenerator.generate(studentEntity)).thenReturn(TOKEN);

        // given
        Mono<LoginResponseDTO> result = loginService.validateGoogleStudentCredentials(studentLoginDTO);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> {
                        assertEquals(ID, response.userId());
                        assertEquals(UNIVERSITY_ID, response.universityId());
                        assertEquals(TOKEN, response.token());
                    })
                    .verifyComplete();
    }

    @Test
    void validateGoogleStudentCredentials_shouldThrowExceptionIfTokenNotValid() {
        var studentLoginDTO = new StudentLoginDTO(ACCESS_TOKEN);
        when(googleTokenValidator.validate(studentLoginDTO)).thenReturn(Mono.error(new InvalidTokenException()));

        // given
        Mono<LoginResponseDTO> result = loginService.validateGoogleStudentCredentials(studentLoginDTO);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(InvalidTokenException.class)
                    .verify();
    }

    @Test
    void validateFacebookStudentCredentials_shouldReturnTokenOnSuccessAuthentication() {
        // when
        var studentLoginDTO = new StudentLoginDTO(ACCESS_TOKEN);
        var studentEntity = createStudent();
        when(facebookTokenValidator.validate(studentLoginDTO)).thenReturn(Mono.just(studentEntity));
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Mono.just(studentEntity));
        when(studentRepository.save(studentEntity)).thenReturn(Mono.error(IllegalStateException::new));
        when(jwtGenerator.generate(studentEntity)).thenReturn(TOKEN);

        // given
        Mono<LoginResponseDTO> result = loginService.validateFacebookStudentCredentials(studentLoginDTO);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> {
                        assertEquals(ID, response.userId());
                        assertEquals(UNIVERSITY_ID, response.universityId());
                        assertEquals(TOKEN, response.token());
                    })
                    .verifyComplete();
    }

    @Test
    void validateFacebookStudentCredentials_shouldSaveUserIfLoginFirstTime() {
        var studentLoginDTO = new StudentLoginDTO(ACCESS_TOKEN);
        var studentEntity = createStudent();
        when(facebookTokenValidator.validate(studentLoginDTO)).thenReturn(Mono.just(studentEntity));
        when(studentRepository.findByEmail(EMAIL)).thenReturn(Mono.empty());
        when(studentRepository.save(studentEntity)).thenReturn(Mono.just(studentEntity));
        when(jwtGenerator.generate(studentEntity)).thenReturn(TOKEN);

        // given
        Mono<LoginResponseDTO> result = loginService.validateFacebookStudentCredentials(studentLoginDTO);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .assertNext(response -> {
                        assertEquals(ID, response.userId());
                        assertEquals(UNIVERSITY_ID, response.universityId());
                        assertEquals(TOKEN, response.token());
                    })
                    .verifyComplete();
    }

    @Test
    void validateFacebookStudentCredentials_shouldThrowExceptionIfTokenNotValid() {
        var studentLoginDTO = new StudentLoginDTO(ACCESS_TOKEN);
        when(facebookTokenValidator.validate(studentLoginDTO)).thenReturn(Mono.error(new InvalidTokenException()));

        // given
        Mono<LoginResponseDTO> result = loginService.validateFacebookStudentCredentials(studentLoginDTO);

        // then
        StepVerifier.create(result)
                    .expectSubscription()
                    .expectError(InvalidTokenException.class)
                    .verify();
    }

    private EmployeeEntity createEmployee() {
        return EmployeeEntity.builder()
                             .id(ID)
                             .type(UserType.EMPLOYEE)
                             .firstName("John")
                             .lastName("John")
                             .email(EMAIL)
                             .universityId(UNIVERSITY_ID)
                             .password(PASSWORD)
                             .build();
    }

    private StudentEntity createStudent() {
        return StudentEntity.builder()
                            .id(ID)
                            .firstName("John")
                            .lastName("John")
                            .email(EMAIL)
                            .universityId(UNIVERSITY_ID)
                            .pictureUrl(PICTURE_URL)
                            .build();
    }
}