package com.erapulus.server.applicationuser.mapper;

import com.erapulus.server.TestUtils;
import com.erapulus.server.applicationuser.mapper.EmployeeCreateRequestToEmployeeEntityMapper;
import com.erapulus.server.common.database.UserType;
import com.erapulus.server.employee.database.EmployeeEntity;
import com.erapulus.server.employee.dto.EmployeeCreateRequestDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployeeCreateRequestToEmployeeEntityMapperTest {

    public static final UserType USER_TYPE = UserType.EMPLOYEE;
    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String PASSWORD = "password";
    private static final String PHONE_NUMBER = "+48123456789";
    private static final Integer UNIVERSITY_ID = 1;

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        EmployeeCreateRequestDto requestDto = EmployeeCreateRequestDto.builder()
                                                                      .email(EMAIL)
                                                                      .firstName(FIRST_NAME)
                                                                      .lastName(LAST_NAME)
                                                                      .phoneNumber(PHONE_NUMBER)
                                                                      .universityId(UNIVERSITY_ID)
                                                                      .password(PASSWORD)
                                                                      .build();

        // when
        EmployeeEntity result = EmployeeCreateRequestToEmployeeEntityMapper.from(requestDto, USER_TYPE);

        //then
        assertNull(result.id());
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(UNIVERSITY_ID, result.universityId());
        assertEquals(USER_TYPE, result.type());
        assertEquals(PHONE_NUMBER, result.phoneNumber());
        assertNull(result.password());
        assertThat(TestUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "password"));
    }
}
