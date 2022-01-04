package com.erapulus.server.mapper.employee;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.dto.employee.EmployeeRequestDto;
import com.erapulus.server.mapper.RequestDtoToEntityMapper;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRequestDtoToEntityMapper implements RequestDtoToEntityMapper<EmployeeRequestDto, EmployeeEntity> {

    public EmployeeEntity from(EmployeeRequestDto employeeRequestDto) {
        return EmployeeEntity.builder()
                             .firstName(employeeRequestDto.firstName())
                             .lastName(employeeRequestDto.lastName())
                             .email(employeeRequestDto.email())
                             .phoneNumber(employeeRequestDto.phoneNumber())
                             .build();
    }
}