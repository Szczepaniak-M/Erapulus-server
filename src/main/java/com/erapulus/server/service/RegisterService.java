package com.erapulus.server.service;

import com.erapulus.server.database.model.UserType;
import com.erapulus.server.mapper.EmployeeEntityToResponseDtoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import com.erapulus.server.database.repository.EmployeeRepository;
import com.erapulus.server.dto.EmployeeCreateRequestDto;
import com.erapulus.server.dto.EmployeeResponseDto;
import com.erapulus.server.mapper.EmployeeCreateRequestToEmployeeEntityMapper;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class RegisterService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmployeeRepository employeeRepository;
    private final EmployeeEntityToResponseDtoMapper employeeEntityToResponseDtoMapper;

    public Mono<EmployeeResponseDto> createAdministrator(@Valid EmployeeCreateRequestDto employeeCreateRequestDto) {
        return createEmployee(employeeCreateRequestDto, UserType.ADMINISTRATOR);
    }

    public Mono<EmployeeResponseDto> createUniversityAdministrator(@Valid EmployeeCreateRequestDto employeeCreateRequestDto) {
        return createEmployee(employeeCreateRequestDto, UserType.UNIVERSITY_ADMINISTRATOR);
    }

    public Mono<EmployeeResponseDto> createUniversityEmployee(@Valid EmployeeCreateRequestDto employeeCreateRequestDto) {
        return createEmployee(employeeCreateRequestDto, UserType.EMPLOYEE);
    }

    private Mono<EmployeeResponseDto> createEmployee(EmployeeCreateRequestDto employeeCreateRequestDto, UserType userType) {
        String encryptedPassword = bCryptPasswordEncoder.encode(employeeCreateRequestDto.password());
        employeeCreateRequestDto.password("");
        return Mono.just(employeeCreateRequestDto)
                   .map(dto -> EmployeeCreateRequestToEmployeeEntityMapper.from(dto, userType))
                   .map(employee -> employee.password(encryptedPassword))
                   .flatMap(employeeRepository::save)
                   .map(employeeEntityToResponseDtoMapper::from)
                   .doOnError(DataIntegrityViolationException.class, e -> log.info("Duplicated email for request:" + employeeCreateRequestDto))
                   .doOnError(e -> log.info("Unexpected error" + e.getMessage()));
    }
}
