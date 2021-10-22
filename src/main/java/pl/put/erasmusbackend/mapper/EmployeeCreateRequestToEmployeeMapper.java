package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.Employee;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeCreateRequestToEmployeeMapper {

    public static Employee from(EmployeeCreateRequestDto employeeCreateRequestDto) {
        return Employee.builder()
                       .firstName(employeeCreateRequestDto.firstName())
                       .lastName(employeeCreateRequestDto.lastName())
                       .email(employeeCreateRequestDto.email())
                       .universityId(employeeCreateRequestDto.universityId())
                       .build();
    }
}
