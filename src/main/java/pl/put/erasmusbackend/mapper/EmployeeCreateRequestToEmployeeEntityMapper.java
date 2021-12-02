package pl.put.erasmusbackend.mapper;

import org.springframework.stereotype.Component;
import pl.put.erasmusbackend.database.model.EmployeeEntity;
import pl.put.erasmusbackend.database.model.UserType;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;

@Component
public class EmployeeCreateRequestToEmployeeEntityMapper implements RequestDtoToEntityMapper <EmployeeCreateRequestDto, EmployeeEntity> {

    public EmployeeEntity from(EmployeeCreateRequestDto employeeCreateRequestDto) {
        return EmployeeEntity.builder()
                             .type(UserType.EMPLOYEE)
                             .firstName(employeeCreateRequestDto.firstName())
                             .lastName(employeeCreateRequestDto.lastName())
                             .email(employeeCreateRequestDto.email())
                             .universityId(employeeCreateRequestDto.universityId())
                             .build();
    }
}
