package pl.put.erasmusbackend.mapper;

import org.junit.jupiter.api.Test;
import pl.put.erasmusbackend.database.model.Employee;
import pl.put.erasmusbackend.dto.EmployeeCreatedDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeToEmployeeCreatedDtoMapperTest {

    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String PASSWORD = "password";
    private static final Integer ID = 1;
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        Employee employee = Employee.builder()
                                    .password(PASSWORD)
                                    .id(ID)
                                    .email(EMAIL)
                                    .firstName(FIRST_NAME)
                                    .lastName(LAST_NAME)
                                    .universityId(UNIVERSITY_ID)
                                    .build();

        // when
        EmployeeCreatedDto result = EmployeeToEmployeeCreatedDtoMapper.from(employee);

        //then
        assertEquals(ID, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(UNIVERSITY_ID, result.universityId());
    }
}