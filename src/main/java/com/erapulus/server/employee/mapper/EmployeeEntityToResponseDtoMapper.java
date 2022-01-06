package com.erapulus.server.employee.mapper;

import com.erapulus.server.common.mapper.EntityToResponseDtoMapper;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeEntityToResponseDtoMapper implements EntityToResponseDtoMapper<EmployeeEntity, EmployeeResponseDto> {

    public EmployeeResponseDto from(EmployeeEntity employeeEntity) {
        return EmployeeResponseDto.builder()
                                  .id(employeeEntity.id())
                                  .type(employeeEntity.type())
                                  .firstName(employeeEntity.firstName())
                                  .lastName(employeeEntity.lastName())
                                  .email(employeeEntity.email())
                                  .universityId(employeeEntity.universityId())
                                  .phoneNumber(employeeEntity.phoneNumber())
                                  .build();
    }
}
