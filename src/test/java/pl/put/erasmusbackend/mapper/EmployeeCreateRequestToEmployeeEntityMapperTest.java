package pl.put.erasmusbackend.mapper;

import org.junit.jupiter.api.Test;
import pl.put.erasmusbackend.database.model.EmployeeEntity;
import pl.put.erasmusbackend.dto.EmployeeCreateRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployeeCreateRequestToEmployeeEntityMapperTest {

    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String PASSWORD = "password";
    private static final Integer UNIVERSITY_ID = 1;

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        EmployeeCreateRequestDto requestDto = EmployeeCreateRequestDto.builder()
                                                                      .email(EMAIL)
                                                                      .firstName(FIRST_NAME)
                                                                      .lastName(LAST_NAME)
                                                                      .universityId(UNIVERSITY_ID)
                                                                      .password(PASSWORD)
                                                                      .build();
        var employeeCreateRequestToEmployeeEntityMapper = new EmployeeCreateRequestToEmployeeEntityMapper();

        // when
        EmployeeEntity result = employeeCreateRequestToEmployeeEntityMapper.from(requestDto);

        //then
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertNull(result.password());
    }
}
