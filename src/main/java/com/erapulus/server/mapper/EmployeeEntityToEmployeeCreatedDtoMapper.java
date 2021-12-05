package com.erapulus.server.mapper;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.dto.EmployeeCreatedDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEntityToEmployeeCreatedDtoMapper implements EntityToResponseDtoMapper<EmployeeEntity, EmployeeCreatedDto> {

    public EmployeeCreatedDto from(EmployeeEntity employeeEntity) {
        return EmployeeCreatedDto.builder()
                                 .id(employeeEntity.id())
                                 .firstName(employeeEntity.firstName())
                                 .lastName(employeeEntity.lastName())
                                 .email(employeeEntity.email())
                                 .universityId(employeeEntity.universityId())
                                 .build();
    }
}
