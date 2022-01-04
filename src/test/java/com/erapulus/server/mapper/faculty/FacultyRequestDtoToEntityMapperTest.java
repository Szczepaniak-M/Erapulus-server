package com.erapulus.server.mapper.faculty;

import com.erapulus.server.database.model.FacultyEntity;
import com.erapulus.server.dto.faculty.FacultyRequestDto;
import com.erapulus.server.mapper.ValidatorUtils;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FacultyRequestDtoToEntityMapperTest {

    private static final String NAME = "name";
    private static final String EMAIL = "example@gmail.com";
    private static final String ADDRESS = "address";

    @Test
    void from_shouldMapDtoToEntity() {
        // given
        FacultyRequestDto requestDto = FacultyRequestDto.builder()
                                                        .name(NAME)
                                                        .address(ADDRESS)
                                                        .email(EMAIL)
                                                        .build();

        // when
        FacultyEntity result = new FacultyRequestDtoToEntityMapper().from(requestDto);

        //then
        assertNull(result.id());
        assertEquals(NAME, result.name());
        assertEquals(EMAIL, result.email());
        assertEquals(ADDRESS, result.address());
        assertNull(result.universityId());
        assertThat(ValidatorUtils.getValidationResult(result)).containsExactlyInAnyOrderElementsOf(List.of("id", "universityId"));
    }
}