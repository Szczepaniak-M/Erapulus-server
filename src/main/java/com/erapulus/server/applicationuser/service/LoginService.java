package com.erapulus.server.applicationuser.service;

import com.erapulus.server.applicationuser.dto.LoginResponseDTO;
import com.erapulus.server.common.exception.InvalidPasswordException;
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
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Service
@Validated
@AllArgsConstructor
public class LoginService {

    private final EmployeeRepository employeeRepository;
    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtGenerator jwtGenerator;
    private final GoogleTokenValidator googleTokenValidator;
    private final FacebookTokenValidator facebookTokenValidator;

    public Mono<LoginResponseDTO> validateEmployeeCredentials(@Valid EmployeeLoginDTO employeeLoginDTO) {
        return employeeRepository.findByEmailAndType(employeeLoginDTO.email())
                                 .switchIfEmpty(Mono.error(new NoSuchUserException()))
                                 .flatMap(employee -> validatePassword(employeeLoginDTO, employee));
    }

    public Mono<LoginResponseDTO> validateGoogleStudentCredentials(@Valid StudentLoginDTO studentLoginDTO) {
        return googleTokenValidator.validate(studentLoginDTO)
                                   .flatMap(this::validateToken);
    }

    public Mono<LoginResponseDTO> validateFacebookStudentCredentials(@Valid StudentLoginDTO studentLoginDTO) {
        return facebookTokenValidator.validate(studentLoginDTO)
                                     .flatMap(this::validateToken);
    }

    private Mono<LoginResponseDTO> validatePassword(EmployeeLoginDTO employeeLoginDTO, EmployeeEntity employeeEntity) {
        boolean isPasswordValid = bCryptPasswordEncoder.matches(employeeLoginDTO.password(), employeeEntity.password());
        if (isPasswordValid) {
            LoginResponseDTO responseDTO = LoginResponseDTO.builder()
                                                           .userId(employeeEntity.id())
                                                           .universityId(employeeEntity.universityId())
                                                           .token(jwtGenerator.generate(employeeEntity))
                                                           .build();
            return Mono.just(responseDTO);
        } else {
            return Mono.error(new InvalidPasswordException());
        }
    }

    private Mono<LoginResponseDTO> validateToken(StudentEntity studentEntity) {
        return studentRepository.findByEmail(studentEntity.email())
                                .switchIfEmpty(studentRepository.save(studentEntity))
                                .map(student -> LoginResponseDTO.builder()
                                                                .userId(student.id())
                                                                .universityId(student.universityId())
                                                                .token(jwtGenerator.generate(student))
                                                                .build());
    }
}
