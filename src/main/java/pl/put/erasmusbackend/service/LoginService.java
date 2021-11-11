package pl.put.erasmusbackend.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import pl.put.erasmusbackend.database.model.EmployeeEntity;
import pl.put.erasmusbackend.database.model.StudentEntity;
import pl.put.erasmusbackend.database.repository.EmployeeRepository;
import pl.put.erasmusbackend.database.repository.StudentRepository;
import pl.put.erasmusbackend.dto.EmployeeLoginDTO;
import pl.put.erasmusbackend.dto.LoginResponseDTO;
import pl.put.erasmusbackend.dto.StudentLoginDTO;
import pl.put.erasmusbackend.security.GoogleTokenValidator;
import pl.put.erasmusbackend.security.JwtGenerator;
import pl.put.erasmusbackend.service.exception.InvalidPasswordException;
import pl.put.erasmusbackend.service.exception.NoSuchUserException;
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

    public Mono<LoginResponseDTO> validateEmployeeCredentials(@Valid EmployeeLoginDTO employeeLoginDTO) {
        return employeeRepository.findByEmail(employeeLoginDTO.email())
                                 .switchIfEmpty(Mono.error(new NoSuchUserException()))
                                 .flatMap(employee -> validatePassword(employeeLoginDTO, employee));
    }

    public Mono<LoginResponseDTO> validateGoogleStudentCredentials(@Valid StudentLoginDTO studentLoginDTO) {
        return googleTokenValidator.validate(studentLoginDTO)
                                   .flatMap(this::validateToken);
    }

//    TODO add facebook login
//    public Mono<LoginResponseDTO> validateFacebookStudentCredentials(@Valid StudentLoginDTO studentLoginDTO) {
//        return facebookTokenValidator.validate(studentLoginDTO)
//                                     .flatMap(this::validateToken);
//    }

    private Mono<LoginResponseDTO> validatePassword(EmployeeLoginDTO employeeLoginDTO, EmployeeEntity employeeEntity) {
        boolean isPasswordValid = bCryptPasswordEncoder.matches(employeeLoginDTO.password(), employeeEntity.password());
        if (isPasswordValid) {
            LoginResponseDTO responseDTO = LoginResponseDTO.builder()
                                                           .userId(employeeEntity.id())
                                                           .token(jwtGenerator.generate(employeeEntity.email()))
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
                                                                .token(jwtGenerator.generate(student.email()))
                                                                .build());
    }
}
