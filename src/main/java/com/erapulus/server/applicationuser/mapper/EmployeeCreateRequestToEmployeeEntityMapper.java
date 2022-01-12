package com.erapulus.server.applicationuser.mapper;

import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.dto.EmployeeCreateRequestDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeCreateRequestToEmployeeEntityMapper {

    public static EmployeeEntity from(EmployeeCreateRequestDto employeeCreateRequestDto, UserType userType) {
        return EmployeeEntity.builder()
                             .type(userType)
                             .firstName(employeeCreateRequestDto.firstName())
                             .lastName(employeeCreateRequestDto.lastName())
                             .email(employeeCreateRequestDto.email())
                             .universityId(employeeCreateRequestDto.universityId())
                             .phoneNumber(employeeCreateRequestDto.phoneNumber())
                             .build();
    }
}
