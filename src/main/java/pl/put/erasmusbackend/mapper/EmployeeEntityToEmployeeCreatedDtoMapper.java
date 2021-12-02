package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.EmployeeEntity;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;

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
