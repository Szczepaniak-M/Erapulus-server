package com.erapulus.server.employee.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.dto.EmployeeResponseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeEntityToResponseDtoMapperTest {

    public static final UserType USER_TYPE = UserType.EMPLOYEE;
    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String PASSWORD = "password";
    private static final String PHONE_NUMBER = "+48123456789";
    private static final Integer ID = 1;
    private static final Integer UNIVERSITY_ID = 2;

    @Test
    void from_shouldMapEntityToDto() {
        // given
        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                                                      .type(USER_TYPE)
                                                      .password(PASSWORD)
                                                      .id(ID)
                                                      .email(EMAIL)
                                                      .firstName(FIRST_NAME)
                                                      .lastName(LAST_NAME)
                                                      .phoneNumber(PHONE_NUMBER)
                                                      .universityId(UNIVERSITY_ID)
                                                      .build();

        // when
        EmployeeResponseDto result = new EmployeeEntityToResponseDtoMapper().from(employeeEntity);

        //then
        assertEquals(ID, result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(PHONE_NUMBER, result.phoneNumber());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertEquals(USER_TYPE, result.type());
        assertTrue(TestUtils.createValidator().validate(result).isEmpty());
    }
}
