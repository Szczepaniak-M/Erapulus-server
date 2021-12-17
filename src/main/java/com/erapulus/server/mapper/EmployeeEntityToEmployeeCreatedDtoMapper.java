package com.erapulus.server.mapper;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.dto.EmployeeCreatedDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeEntityToEmployeeCreatedDtoMapper {

    public static EmployeeCreatedDto from(EmployeeEntity employeeEntity) {
        return EmployeeCreatedDto.builder()
                                 .id(employeeEntity.id())
                                 .type(employeeEntity.type())
                                 .firstName(employeeEntity.firstName())
                                 .lastName(employeeEntity.lastName())
                                 .email(employeeEntity.email())
                                 .universityId(employeeEntity.universityId())
                                 .build();
    }
}
