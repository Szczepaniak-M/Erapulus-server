package com.erapulus.server.applicationuser.service;

import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeRepository;
import com.erapulus.server.employee.dto.EmployeeCreateRequestDto;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import com.erapulus.server.applicationuser.mapper.EmployeeCreateRequestToEmployeeEntityMapper;
import com.erapulus.server.employee.mapper.EmployeeEntityToResponseDtoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
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
                   .map(employeeEntityToResponseDtoMapper::from);
    }
}
