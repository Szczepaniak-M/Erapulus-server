package com.erapulus.server.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.dto.EmployeeCreateRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeCreateRequestToEmployeeEntityMapper {

    public static EmployeeEntity from(EmployeeCreateRequestDto employeeCreateRequestDto, UserType userType) {
        return EmployeeEntity.builder()
                             .type(userType)
                             .firstName(employeeCreateRequestDto.firstName())
                             .lastName(employeeCreateRequestDto.lastName())
                             .email(employeeCreateRequestDto.email())
                             .universityId(employeeCreateRequestDto.universityId())
                             .build();
    }
}
