package pl.put.erasmusbackend.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import pl.put.erasmusbackend.database.model.EmployeeEntity;
import pl.put.erasmusbackend.database.model.UserType;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmployeeCreateRequestToEmployeeEntityMapper {

    public static EmployeeEntity from(EmployeeCreateRequestDto employeeCreateRequestDto) {
        return EmployeeEntity.builder()
                             .type(UserType.EMPLOYEE)
                             .firstName(employeeCreateRequestDto.firstName())
                             .lastName(employeeCreateRequestDto.lastName())
                             .email(employeeCreateRequestDto.email())
                             .universityId(employeeCreateRequestDto.universityId())
                             .build();
    }
}
