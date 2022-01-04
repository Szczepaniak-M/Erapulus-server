package com.erapulus.server.mapper.employee;

import com.erapulus.server.database.model.EmployeeEntity;
import com.erapulus.server.dto.employee.EmployeeRequestDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeRequestDtoToEntityMapperTest {

    private static final String EMAIL = "example@gmail.com";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "firstName";
    private static final String PHONE_NUMBER = "+48123456789";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        EmployeeRequestDto requestDto = EmployeeRequestDto.builder()
                                                          .email(EMAIL)
                                                          .firstName(FIRST_NAME)
                                                          .lastName(LAST_NAME)
                                                          .phoneNumber(PHONE_NUMBER)
                                                          .build();

        // when
        EmployeeEntity result = new EmployeeRequestDtoToEntityMapper().from(requestDto);

        //then
        assertEquals(EMAIL, result.email());
        assertEquals(FIRST_NAME, result.firstName());
        assertEquals(LAST_NAME, result.lastName());
        assertEquals(PHONE_NUMBER, result.phoneNumber());
        assertThat(ValidatorUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "password", "type"));
    }
}