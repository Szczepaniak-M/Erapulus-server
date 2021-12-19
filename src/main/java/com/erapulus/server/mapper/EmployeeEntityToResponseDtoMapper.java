package com.erapulus.server.mapper;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.dto.EmployeeResponseDto;
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
