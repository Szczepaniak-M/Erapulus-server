package com.erapulus.server.employee.mapper;

import com.erapulus.server.common.mapper.RequestDtoToEntityMapper;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.dto.EmployeeRequestDto;
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
