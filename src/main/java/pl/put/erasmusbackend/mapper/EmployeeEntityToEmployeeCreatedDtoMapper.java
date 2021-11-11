package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.EmployeeEntity;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeEntityToEmployeeCreatedDtoMapper {

    public static EmployeeCreatedDto from(EmployeeEntity employeeEntity) {
        return EmployeeCreatedDto.builder()
                                 .id(employeeEntity.id())
                                 .firstName(employeeEntity.firstName())
                                 .lastName(employeeEntity.lastName())
                                 .email(employeeEntity.email())
                                 .universityId(employeeEntity.universityId())
                                 .build();
    }
}
