package com.erapulus.server.mapper;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.dto.EmployeeCreateRequestDto;
import org.junit.jupiter.api.Assertions;
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
        var employeeCreateRequestToEmployeeEntityMapper = new EmployeeCreateRequestToEmployeeEntityMapper();

        // when
        EmployeeEntity result = employeeCreateRequestToEmployeeEntityMapper.from(requestDto);

        //then
        Assertions.assertEquals(EMAIL, result.email());
        Assertions.assertEquals(FIRST_NAME, result.firstName());
        Assertions.assertEquals(LAST_NAME, result.lastName());
        Assertions.assertEquals(UNIVERSITY_ID, result.universityId());
        assertNull(result.password());
    }
}
