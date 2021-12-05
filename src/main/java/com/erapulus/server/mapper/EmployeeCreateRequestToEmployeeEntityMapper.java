package com.erapulus.server.mapper;

import org.springframework.stereotype.Component;
import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.dto.EmployeeCreateRequestDto;

@Component
public class EmployeeCreateRequestToEmployeeEntityMapper implements RequestDtoToEntityMapper <EmployeeCreateRequestDto, EmployeeEntity> {

    public EmployeeEntity from(EmployeeCreateRequestDto employeeCreateRequestDto) {
        return EmployeeEntity.builder()
                             .type(UserType.EMPLOYEE)
                             .firstName(employeeCreateRequestDto.firstName())
                             .lastName(employeeCreateRequestDto.lastName())
                             .email(employeeCreateRequestDto.email())
                             .universityId(employeeCreateRequestDto.universityId())
                             .build();
    }
}
