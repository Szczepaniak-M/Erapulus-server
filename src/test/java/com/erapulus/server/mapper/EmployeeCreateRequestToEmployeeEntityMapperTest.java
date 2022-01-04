package com.erapulus.server.mapper;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.database.model.UserType;
import com.erapulus.server.dto.employee.EmployeeCreateRequestDto;
import org.junit.jupiter.api.Test;

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

        // when
        EmployeeEntity result = EmployeeCreateRequestToEmployeeEntityMapper.from(requestDto, UserType.EMPLOYEE);

        //then
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertEquals(UserType.EMPLOYEE, result.type());
        assertNull(result.password());
    }
}
