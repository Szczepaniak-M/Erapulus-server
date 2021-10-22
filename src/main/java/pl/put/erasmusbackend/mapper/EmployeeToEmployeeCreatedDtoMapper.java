package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.Employee;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeToEmployeeCreatedDtoMapper {

    public static EmployeeCreatedDto from(Employee employee) {
        return EmployeeCreatedDto.builder()
                                 .id(employee.id())
                                 .firstName(employee.firstName())
                                 .lastName(employee.lastName())
                                 .email(employee.email())
                                 .universityId(employee.universityId())
                                 .build();
    }
}
